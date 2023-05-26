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
        String insertQuery = "INSERT INTO cars (manufacturer_id, model) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                        connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            if (generatedKey.next()) {
                car.setId(generatedKey.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert to DB car: " + car, e);
        }
        if (car.getId() != null && car.getDrivers() != null && car.getDrivers().size() > 0) {
            addDriverCarRelations(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, m.id AS manufacturer_id, model, name, country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car with ID: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars c INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        if (!cars.isEmpty()) {
            cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cd.car_id FROM cars_drivers cd "
                + "JOIN drivers d ON d.id = cd.driver_id "
                + "WHERE cd.driver_id = ? AND d.is_deleted = FALSE;";
        List<Long> carIdList = new ArrayList<>();
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carIdList.add(resultSet.getObject("car_id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't get cars IDs from DB by the driver ID: " + driverId, e);
        }
        if (!carIdList.isEmpty()) {
            carIdList.forEach(carId -> get(carId).ifPresent(cars::add));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        deleteDriverCarRelations(car.getId());
        if (car.getDrivers() != null && car.getDrivers().size() > 0) {
            addDriverCarRelations(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete the car with ID: " + id, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getObject("car_id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(
                        resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")), null);
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("driver_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void addDriverCarRelations(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't add drivers: " + car.getDrivers()
                            + " to the car with ID: " + car.getId(), e);
        }
    }

    private void deleteDriverCarRelations(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete drivers and cars relations for car with ID: " + carId, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT d.id AS driver_id, name, license_number "
                + "FROM drivers d INNER JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't get drivers for the car with ID: " + carId, e);
        }
        return drivers;
    }
}
