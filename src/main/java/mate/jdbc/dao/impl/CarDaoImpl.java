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
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void addDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
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
