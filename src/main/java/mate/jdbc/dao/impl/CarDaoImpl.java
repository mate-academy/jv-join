package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement((query),
                        Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add to DB: " + car, e);
        }
        createDriverCarPairs(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car == null) {
            return Optional.empty();
        }
        car.setDrivers(getDriversForCar(id));
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't form a list of cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT car_id FROM cars_drivers cd WHERE cd.driver_id = ?";
        List<Long> carIds = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carIds.add(resultSet.getObject("car_id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't form a list of cars for driver id "
                    + driverId, e);
        }
        return carIds.stream().map(id -> get(id).get()).collect(Collectors.toList());
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getManufacturer().getId());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setLong(3, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update info for " + car, e);
        }
        deleteDriverCarPairs(car.getId());
        createDriverCarPairs(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        int successfulOperations = 0;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            successfulOperations = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't perform deletion from DB for car id " + id, e);
        }
        deleteDriverCarPairs(id);
        return successfulOperations > 0;
    }

    private void createDriverCarPairs(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(1, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add to DB: " + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT d.id AS driver_id, d.name AS name, d.license_number AS license " 
                + "FROM cars_drivers cd JOIN drivers d "
                + "ON cd.driver_id = d.id WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection(); 
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver for carID " + carId, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("driver_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license"));
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getObject("c.id", Long.class),
                resultSet.getString("model"),
                getManufacturerFromResultSet(resultSet));
    }

    private Manufacturer getManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        return new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
    }

    private void deleteDriverCarPairs(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't perform deletion from DB for car id "
                    + carId, e);
        }
    }
}
