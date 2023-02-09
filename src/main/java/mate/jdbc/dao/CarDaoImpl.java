package mate.jdbc.dao;

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
import java.util.Optional;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        return null;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, model, m.name AS name, m.id AS manufacturer_id, m.country AS country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                 car = parseCarWithManufacturerFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"), resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
            String getAllDriversForCarRequest = "SELECT cd.id, name, licenseNumber"
                    + " FROM drivers d JOIN cars_drivers cd "
                    + "ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getAllDriversForCarRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("licenseNumber"));
    }
}
