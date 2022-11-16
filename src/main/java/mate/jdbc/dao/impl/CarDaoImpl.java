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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(2, car.getManufacturer().getId());
            statement.setString(1, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create a car "
                    + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT* FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT*"
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        deleteRelationsForCar(car.getId());
        insertDriversIntoCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.*, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE "
                + "AND cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all by driver id " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("manufacturers.id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void deleteRelationsForCar(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars drivers relations by car id "
                    + id, e);
        }
    }

    private void insertDriversIntoCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers into car " + car, e);
        }
    }
}
