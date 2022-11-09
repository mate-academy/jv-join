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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t create car in carDB", e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS cars_id, "
                + "cars.model AS model, "
                + "manufacturers.id AS manufacturers_id, "
                + "manufacturers.name AS manufacturers_name, "
                + "manufacturers.country AS manufacturers_country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? "
                + "AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
                car.setDrivers(getDrivers(car.getId(), connection));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t get car from carDB", e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id AS cars_id, "
                + "cars.model AS model, "
                + "manufacturers.id AS manufacturers_id, "
                + "manufacturers.name AS manufacturers_name, "
                + "manufacturers.country AS manufacturers_country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                car.setDrivers(getDrivers(car.getId(), connection));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
            updateDrivers(car, connection);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update cat from carsDB", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t delete car from carsDB", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id AS cars_id, "
                + "cars.model AS model, "
                + "manufacturers.id AS manufacturers_id, "
                + "manufacturers.name AS manufacturers_name, "
                + "manufacturers.country AS manufacturers_country "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + " WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                car.setDrivers(getDrivers(car.getId(), connection));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driver", e);
        }
    }

    private List<Driver> getDrivers(Long carId, Connection connection) {
        String query = "SELECT * FROM drivers "
                + "JOIN cars_drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers from driverDB", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private void updateDrivers(Car car, Connection connection) throws SQLException {
        List<Driver> drivers = car.getDrivers();
        String removeQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        PreparedStatement preparedStatementRemove = connection.prepareStatement(removeQuery);
        preparedStatementRemove.setLong(1, car.getId());
        preparedStatementRemove.executeUpdate();

        String addQuery = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        PreparedStatement preparedStatementAdd = connection.prepareStatement(addQuery);
        for (Driver driver : drivers) {
            preparedStatementAdd.setLong(1, driver.getId());
            preparedStatementAdd.setLong(2, car.getId());
            preparedStatementAdd.executeUpdate();
        }
    }

    private Car getCar(ResultSet resultSet) {
        try {
            Car car = new Car();
            car.setId(resultSet.getObject("cars_id", Long.class));
            car.setModel(resultSet.getString("model"));
            car.setManufacturer(getManufacturer(resultSet));
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Parsing result set for car error", e);
        }

    }

    private Manufacturer getManufacturer(ResultSet resultSet) {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
            manufacturer.setName(resultSet.getString("manufacturers_name"));
            manufacturer.setCountry(resultSet.getString("manufacturers_country"));
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Parsing result set for manufacturer error");
        }
    }
}
