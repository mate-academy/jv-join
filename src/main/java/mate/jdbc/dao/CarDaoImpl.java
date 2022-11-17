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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        String queryDrivers = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                         connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement statementDrivers =
                         connection.prepareStatement(queryDrivers)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
            statementDrivers.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statementDrivers.setLong(2, driver.getId());
                statementDrivers.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot create the car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars "
                + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCar(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get car. id=" + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars "
                + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
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
            throw new DataProcessingException("Cannot get all drivers", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            addDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update the car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete car. id=" + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars_drivers WHERE driver_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                Optional<Car> car = get(resultSet.getObject("car_id", Long.class));
                cars.add(car.get());
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get the cars with driverId: " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        return new Car(
                carId,
                resultSet.getString("model"),
                getManufacturer(resultSet),
                getCarDrivers(carId)
        );
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        return new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country")
        );
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("driver_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number")
        );
    }

    private List<Driver> getCarDrivers(Long carId) throws SQLException {
        String query = "SELECT * FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND cars_drivers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        }
    }

    private void addDrivers(Car car) throws SQLException {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        String queryRemove = "UPDATE cars_drivers SET is_deleted = TRUE WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                PreparedStatement removeStatement = connection.prepareStatement(queryRemove)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        }
    }
}
