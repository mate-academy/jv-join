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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            insertDriversToDataBase(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS car_id, model, "
                + "name, country, manufacturers.id AS manufacturer_id "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarByResultSet(resultSet);
            }
            car.setDrivers(getDriversByCarId(id));
            return Optional.of(car);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT car.id AS car_id, model, "
                + "name, country, manufacturer.id AS manufacturer_id "
                + "FROM cars car "
                + "JOIN manufacturers manufacturer "
                + "ON c.manufacturer_id = manufacturer.id "
                + "WHERE car.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarByResultSet(resultSet);
                car.setDrivers(getDriversByCarId(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from db.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = 0;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(3, car.getId());
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            updateDriversForCar(car);
            return car;
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car in database " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars "
                + "SET is_deleted = 1 "
                + "WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id " + id + " from database", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT car.id AS car_id, model, "
                + "name, country, manufacturer.id AS manufacturer_id "
                + "FROM cars car "
                + "JOIN cars_drivers cd "
                + "ON car.id = cd.car_id "
                + "JOIN manufacturers manufacturer "
                + "ON car.manufacturer_id = manufacturer.id "
                + "WHERE car.is_deleted = FALSE AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarByResultSet(resultSet);
                car.setDrivers(getDriversByCarId(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars by driver with id " + driverId, e);
        }
    }

    private void insertDriversToDataBase(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id)"
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to DB. " + car.getDrivers(), e);
        }
    }

    private Car getCarByResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("car_id"));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("country"),
                resultSet.getString("name")
        );
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversByCarId(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT id, name, license_number "
                + "FROM cars_drivers "
                + "JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers by car id. " + id, e);
        }
    }
    
    private void updateDriversForCar(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) "
                + "VALUES (?, ?)";
        deleteDriversByCarId(car.getId());
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't update drivers for car " + car, e);
        }
    }

    private void deleteDriversByCarId(Long id) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete drivers from database my car id " + id, e);
        }
    }
}
