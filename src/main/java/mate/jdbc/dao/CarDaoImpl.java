package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car creare(Car car) {
        String insertRequst = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertRequst, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();

            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ".");
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.id as car_id, model, mn.id as manufacturer_id,"
                + " mn.name as manufacturer FROM cars c JOIN manufacturers mn "
                + "ON c.manufacturer_id = mn.id WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResltSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn`t get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public boolean delete(Long carId) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement
                        = connection.prepareStatement(deleteQuery)) {
            softDeleteCarStatement.setLong(1, carId);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + carId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
            deleteCarsDriversRelations(car);
            insertDrivers(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car " + car, e);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id as car_id, model, mn.id as manufacturer_id,"
                + " mn.name as manufacturer FROM cars c JOIN manufacturers mn "
                + "ON c.manufacturer_id = mn.id WHERE c.is_deleted = false;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCarWithManufacturerFromResltSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn`t get all cars ", e);
        }
        for (Car car: carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT car_id, model, manufacturer_id "
                + "FROM cars_drivers join cars ON cars_drivers.car_id = cars.id "
                + "WHERE driver_id = ? AND is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriversStatement
                        = connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriversStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCarWithManufacturerFromResltSet(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn`t get all cars by driverId " + driverId, e);
        }
    }

    private Car parseCarWithManufacturerFromResltSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversFromCarRequest = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversFromCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseCarsFromResltSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn`t get drivers for car with id " + carId);
        }
    }

    private Driver parseCarsFromResltSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement
                          = connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Coudn`t insert drivers to car " + car);
        }
    }

    private void deleteCarsDriversRelations(Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers where cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement deleteRelationsStatement
                        = connection.prepareStatement(deleteRelationsQuery)) {
            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Coudn`t delete car-driver relations for car " + car);
        }
    }
}
