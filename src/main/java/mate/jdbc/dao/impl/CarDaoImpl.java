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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create this car -" + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id AS car_id, m.id AS manufacturer_id,"
                + " name, country, model"
                + " FROM cars c"
                + " JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE; ";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectRequest)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by this id - " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id AS car_id, m.id AS manufacturer_id,"
                + " name, country, model"
                + " FROM cars c"
                + " JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectRequest)) {
            cars = getListByResultSet(statement.executeQuery());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car", e);
        }
        cars.forEach(x -> x.setDrivers(getAllDrivers(x.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET manufacturer_id = ?, model = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateRequest)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update this car - " + car, e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String updateRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateRequest)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by this id - " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT c.id AS car_id, model, m.name, country, manufacturer_id"
                + " FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE c.is_deleted = false AND driver_id = ? AND d.is_deleted = FALSE;;";
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectRequest)) {
            statement.setLong(1, driverId);
            cars = getListByResultSet(statement.executeQuery());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by this driver id - "
                    + driverId, e);
        }
        cars.forEach(x -> x.setDrivers(getAllDrivers(x.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert this driver to car - " + car, e);
        }
    }

    private void deleteDrivers(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteRequest)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete all drivers which use this car - "
                    + car, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getLong("car_id"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getAllDrivers(Long carId) {
        String selectRequest = "SELECT driver_id, name, license_number"
                + " FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver by this car id - " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setName(resultSet.getString("name"));
        driver.setId(resultSet.getLong("driver_id"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private List<Car> getListByResultSet(ResultSet resultSet) throws SQLException {
        List<Car> cars = new ArrayList<>();
        Car car = null;
        while (resultSet.next()) {
            car = parseCarFromResultSet(resultSet);
            cars.add(car);
        }
        return cars;
    }
}
