package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
                + "WHERE cars.is_deleted = FALSE "
                + "AND cars.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t get car from carDB", e);
        }
        return Optional.ofNullable(car);
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
                cars.add(getCar(resultSet));
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
            if (preparedStatement.executeUpdate() > 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t delete car from carsDB", e);
        }
        return false;
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
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driver", e);
        }
        return cars;
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
        Car car = new Car();
        try {
            car.setId(resultSet.getObject("cars_id", Long.class));
            car.setModel(resultSet.getString("model"));
            car.setManufacturer(getManufacturer(resultSet));
        } catch (SQLException e) {
            throw new DataProcessingException("Parsing result set for car error", e);
        }
        return car;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) {
        Manufacturer manufacturer = new Manufacturer();
        try {
            manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
            manufacturer.setName(resultSet.getString("manufacturers_name"));
            manufacturer.setCountry(resultSet.getString("manufacturers_country"));
        } catch (SQLException e) {
            throw new DataProcessingException("Parsing result set for manufacturer error");
        }
        return manufacturer;
    }
}
