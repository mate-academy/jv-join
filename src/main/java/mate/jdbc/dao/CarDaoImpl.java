package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createQuery = "INSERT INTO cars (model, registration_number, manufacturer_id) "
                + "VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(createQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setString(2, car.getRegistrationNumber());
            createStatement.setLong(3, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                addDriversToCar(car);
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create car: " + car, throwable);
        }
    }

    @Override
    public Car get(Long id) {
        String getQuery = "SELECT c.id AS car_id, model, registration_number, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarByManufacturer(resultSet);
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car by id = " + id, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id, model, registration_number, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarByManufacturer(resultSet));
            }
            for (Car car : cars) {
                car.setDrivers(getAllDriversByCarId(car.getId()));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get list of cars!", throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, registration_number = ?, "
                + "manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        int updateRecordsNumber;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement =
                        connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setString(2, car.getRegistrationNumber());
            updateStatement.setLong(3, car.getManufacturer().getId());
            updateStatement.setLong(4, car.getId());
            updateRecordsNumber = updateStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't update car: " + car, throwable);
        }
        if (updateRecordsNumber > 0) {
            return car;
        }
        throw new DataProcessingException("Can`t the car with id = " + car.getId());
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete car with id = " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllByDriverStatement =
                         connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarByManufacturer(resultSet));
            }
            for (Car car : cars) {
                car.setDrivers(getAllDriversByCarId(car.getId()));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException(
                    "Can't find cars by driver id = " + driverId, throwable);
        }
    }

    private void addDriversToCar(Car car) {
        String insertQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDrivers
                         = connection.prepareStatement(insertQuery)) {
            insertDrivers.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDrivers.setLong(2, driver.getId());
                insertDrivers.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't add driver to car with id = "
                    + car.getId(), throwable);
        }
    }

    private List<Driver> getAllDriversByCarId(Long carId) {
        String findQuery = "SELECT * FROM drivers "
                + "JOIN cars_drivers cd ON drivers.id = cd.driver_id "
                + "WHERE car_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement findStatement = connection.prepareStatement(findQuery)) {
            findStatement.setLong(1, carId);
            ResultSet resultSet = findStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException(
                    "Can't find all drivers for the car with id = " + carId, throwable);
        }
    }

    private Car parseCarByManufacturer(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        String registrationNumber = resultSet.getString("registration_number");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, registrationNumber, manufacturer);
        car.setId(carId);
        return car;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }
}
