package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        return null;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id AS cars.id, cars.model AS model,"
                + "manufacturers.id AS manufacturers.id, manufacturers.name AS manufacturers.name, "
                + "manufacturers.country as manufacturers.country"
                + " FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could`t get car from carDB");
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id AS cars.id, cars.model AS model,"
                + "manufacturers.id AS manufacturers.id, manufacturers.name AS manufacturers.name, "
                + "manufacturers.country as manufacturers.country"
                + " FROM cars JOIN manufacturers "
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
            car.setId(resultSet.getObject("id", Long.class));
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
            manufacturer.setId(resultSet.getObject("manufacturers.id", Long.class));
            manufacturer.setName(resultSet.getString("manufacturers.name"));
            manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        } catch (SQLException e) {
            throw new DataProcessingException("Parsing result set for manufacturer error");
        }
        return manufacturer
    }
}
