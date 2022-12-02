package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarRequest = "INSERT INTO cars (manufacturer_id, model) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(createCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create a car: " + car, e);
        }
        insertCarsDriversRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT c.id as car_id, model, manufacturer_id, name, country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id WHERE c.id = ?"
                + " AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarRequest = "SELECT c.id as car_id, model, "
                + "m.id as manufacturer_id, name, country FROM CARS c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement = connection
                        .prepareStatement(getAllCarRequest)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            for (Car car : allCars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB", e);
        }
        return allCars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateCarRequest)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car: " + car, e);
        }
        removeCarsDriversRelations(car);
        insertCarsDriversRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection
                        .prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, carId);
            return deleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id as car_id, model, manufacturer_id, "
                + "name, country FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id JOIN "
                + "manufacturers m ON m.id = c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> carsList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement = connection
                        .prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            for (Car car : carsList) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from DB by driver with id: "
                    + driverId, e);
        }
        return carsList;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.car_id WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversForCarStatement = connection
                        .prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers for car with id: "
                    + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void insertCarsDriversRelations(Car car) {
        String insertCarsDriversRelationsRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarsDriversRelationsStatement = connection
                        .prepareStatement(insertCarsDriversRelationsRequest)) {
            insertCarsDriversRelationsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertCarsDriversRelationsStatement.setLong(2, driver.getId());
                insertCarsDriversRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car: " + car, e);
        }
    }

    private void removeCarsDriversRelations(Car car) {
        String removeCarsDriversRelationsRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeCarsDriversRelationsStatement = connection
                        .prepareStatement(removeCarsDriversRelationsRequest)) {
            removeCarsDriversRelationsStatement.setObject(1, car.getId());
            removeCarsDriversRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove driver from car: " + car, e);
        }
    }
}
