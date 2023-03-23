package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertCarRequest = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertCarStatement = connection.prepareStatement(insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setObject(2, car.getManufacturer());
            ResultSet resultSet = insertCarStatement.executeQuery();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't insert car " + car + " into DB", e);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String getCarByIdRequest = "SELECT * , manufacturers.name, manufacturers.country FROM cars\n" +
                "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id\n" +
                "WHERE id = ? AND is_deleted = FALSE AND manufacturers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getCarByIdRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSetWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get cat from DB with id " + id, e);
        }
        return car;
    }

    private Car parseCarFromResultSetWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> carsList = new ArrayList<>();
        String getAllCarsRequest = "SELECT *, manufacturers.name, manufacturers.country FROM cars\n" +
                "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id\n" +
                "WHERE is_deleted = FALSE AND manufacturers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarFromResultSetWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't get List of cars from database", e);
        }
        return carsList;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't update the car in the database " + car, e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't delete car from database with id " + id, e);
        }
    }
}
