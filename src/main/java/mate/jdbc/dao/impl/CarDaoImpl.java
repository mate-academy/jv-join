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
        String query = "INSERT INTO cars(manufacturer_id, model) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement = connection
                         .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create car = " + car, throwables);
        }
        createReferenceToCarDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT c.id as car_id, manufacturer_id, model, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car by id = " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE cars.is_deleted = false AND id = ? ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't update car = " + car, throwables);
        }
        deleteReferenceFromCarDrivers(car);
        createReferenceToCarDrivers(car);
        return car;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id as car_id, manufacturer_id, model, name, country "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = false AND cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car by driverId = "
                    + driverId, throwables);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c)));
        return cars;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id as car_id, manufacturer_id, model, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection
                        .prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get all data from DB", throwables);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c)));
        return cars;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() >= 1;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete car from table, "
                    + "by car_id = " + id, throwables);
        }
    }

    private void deleteReferenceFromCarDrivers(Car car) {
        String deleteRelationWithCarDrivers = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(deleteRelationWithCarDrivers)) {
            preparedStatement.setLong(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete reference between drivers "
                    + " and their cars, " + "by car_id = " + car, throwables);
        }
    }

    private void createReferenceToCarDrivers(Car car) {
        String createRelationWithCarDrivers = "INSERT INTO cars_drivers(driver_id, car_id) "
                + "VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(createRelationWithCarDrivers)) {
            List<Driver> drivers = car.getDrivers();
            preparedStatement.setLong(2, car.getId());
            for (Driver driver : drivers) {
                preparedStatement.setLong(1, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't add reference between drivers and their cars, "
                    + "by carId = " + car.getId(), throwables);
        }
    }



    private List<Driver> getDriversForCar(Car car) {
        String query = "SELECT d.id, name, license_number FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = false AND cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement = connection.prepareStatement(query)) {
            getDriversForCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get driver for car id = "
                    + car.getId(), throwables);
        }
    }

    private Driver getDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Manufacturer getManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        return car;
    }
}
