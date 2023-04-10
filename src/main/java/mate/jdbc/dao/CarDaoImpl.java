package mate.jdbc.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String query = "INSERT INTO drivers (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id AS car_id, m.id AS manufacturer_id, model "
                + "FROM cars c JOIN manufacturers m ON c.id = m.id"
                + "WHERE id = ? AND is_deleted = FALSE";
        Car car = null;
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }

        if (car != null) {
            car.setDrivers(getAllByCar(id));
        }
        return car;
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

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(resultSet
                .getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("manufacturer_id", Long.class));
        return car;
    }

    private List<Driver> getAllByCar(Long carId) {
        String getAllDriverForCar = "SELECT id, name, license_number FROM drivers d " +
                "JOIN cars_drivers ca ON d.id = ca.driver_id WHERE car_id = ?;";
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(getAllDriverForCar)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(parserDriverFromResultSet(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id " + carId, e);
        }
    }

    private Driver parserDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }
}
