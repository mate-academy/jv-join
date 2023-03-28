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
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private static ManufacturerService manufacturerService;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id)"
                + " VALUES (?, ?);";
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
            insertDrivers(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars"
                + " WHERE is_deleted = FALSE AND id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new LinkedList<>();
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(long driverId) {
        String query = "SELECT * FROM cars c"
                + " JOIN car_drivers cd ON c.id = cd.car_id"
                + " WHERE driver_id = ?;";
        List<Car> cars = new LinkedList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id: " + driverId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            deleteDrivers(car.getId());
            insertDrivers(car);
            if (statement.executeUpdate() > 0) {
                return car;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            deleteDrivers(id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id, e);
        }
    }

    private void insertDrivers(Car car) throws SQLException {
        String query = "INSERT INTO car_drivers (driver_id, car_id) VALUES (?, ?);";
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(2, car.getId());
        for (Driver driver: car.getDrivers()) {
            statement.setLong(1, driver.getId());
            statement.executeUpdate();
        }
    }

    private static void deleteDrivers(Long id) throws SQLException {
        String query = "DELETE FROM car_drivers WHERE car_id = ?;";
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    private static List<Driver> getDriversByCar(long carId) throws SQLException {
        String query = "select * FROM drivers d"
                + " JOIN car_drivers cd ON d.id = cd.driver_id"
                + " WHERE car_id = ? AND is_deleted = FALSE;";
        List<Driver> drivers = new LinkedList<>();
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, carId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            drivers.add(getDriver(resultSet));
        }
        return drivers;
    }

    private static Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                    resultSet.getString(2),
                    resultSet.getString(3));
    }

    private static Car getCar(ResultSet resultSet) throws SQLException {
        long id = resultSet.getObject("id", Long.class);
        return new Car(resultSet.getObject("id", Long.class),
                resultSet.getString("model"),
                manufacturerService.get(
                        resultSet.getObject("manufacturer_id", Long.class)),
                getDriversByCar(id));
    }
}
