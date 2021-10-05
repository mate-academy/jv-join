package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.List;

public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id AS car_id , model, mf.name "
                + "FROM CARS c JOIN manufacturers mf "
                + "ON c.manufacturer_id = mf.id WHERE c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(selectRequest,
                     Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, id);
            getCarStatement.executeUpdate();
            ResultSet resultSet = getCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Car car = new Car();
                car.setModel(resultSet.getString("model"));
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setName(resultSet.getString("name"));
                car.setManufacturer(manufacturer);
                car.setId(id);
                return car;
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create find car in DB. ", throwable);
        }
        return null;
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

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
