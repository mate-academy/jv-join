package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1,Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant create car with id " + car.getId(), e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT *     "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next()
                    ? Optional.of(parseCar(resultSet))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get all cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getId());
            preparedStatement.setLong(3, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant update car with id " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_delete = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            List<Car> cars = new ArrayList();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get all cars by driver with id " + driverId, e);
        }
    }

    private Car parseCar(ResultSet resultSet) {
        Car car = new Car();
        try {
            car.setId(resultSet.getObject(1, Long.class));
            car.setModel(resultSet.getString("model"));
            String name = resultSet.getString("name");
            String country = resultSet.getString("country");
            Long id = resultSet.getObject("m.id",Long.class); // ATM
            Manufacturer manufacturer = new Manufacturer(id, name, country);
            car.setManufacturer(manufacturer);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant parse car from result set", e);
        }
    }
}
