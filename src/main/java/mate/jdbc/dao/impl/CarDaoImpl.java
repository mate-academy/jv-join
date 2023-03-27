package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private static ManufacturerService manufacturerService;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id)" +
                " VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't create car: " + car, e);
        }
        return new Car(null, null);
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars" +
                " WHERE is_deleted = FALSE AND id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new LinkedList<>();
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from DB", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?" +
                " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            if (statement.executeUpdate() > 0) {
                return car;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car: " + car, e);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car by id: " + id, e);
        }
    }

    private static Car getCar(ResultSet resultSet) throws SQLException {
        return new Car(resultSet.getObject("id", Long.class),
                resultSet.getString("model"),
                manufacturerService.get(
                        resultSet.getObject("manufacturer_id", Long.class)));
    }
}
