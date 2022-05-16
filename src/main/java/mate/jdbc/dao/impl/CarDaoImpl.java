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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id)"
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(insertRequest,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB: " + car, e);
        }
        return createCarsDriversRelation(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM CARS c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement =
                         connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllRequest = "SELECT c.id AS car_id, model,"
                + " m.id AS manufacturer_id, m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllStatement =
                         connection.prepareStatement(selectAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id AS car_id, "
                + "c.model, m.name, m.country, m.id AS manufacturer_id "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsByDriverIdStatement =
                         connection.prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsByDriverIdStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverIdStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB with driver id: "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "where id = ? AND is_deleted = FALSE;";
        deleteCarsDriversRelation(car);
        createCarsDriversRelation(car);
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement
                         = connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in DB" + car, e);
        }
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement softDeleteCarStatement =
                         connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1, carId);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + carId, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id"
                + " WHERE cd.car_id = ? AND d.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement =
                         connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id: " + carId, e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private Car createCarsDriversRelation(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id)"
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarsDriversRelationsStatement
                         = connection.prepareStatement(query)) {
            List<Driver> drivers = car.getDrivers();
            for (Driver driver : drivers) {
                updateCarsDriversRelationsStatement.setLong(1, car.getId());
                updateCarsDriversRelationsStatement.setLong(2, driver.getId());
                updateCarsDriversRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create cars - "
                    + "drivers relations in DB with car: " + car, e);
        }
        return car;
    }

    private void deleteCarsDriversRelation(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement
                         = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete cars - drivers "
                    + "relations in DB with car: " + car, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

}
