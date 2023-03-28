package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final int PARAMETER_OF_FIRST_INDEX = 1;
    private static final int PARAMETER_OF_SECOND_INDEX = 2;
    private static final int PARAMETER_OF_THIRD_INDEX = 3;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(PARAMETER_OF_FIRST_INDEX, car.getModel());
            statement.setObject(PARAMETER_OF_SECOND_INDEX, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(PARAMETER_OF_FIRST_INDEX, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ".", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id AS id, model, manufacturer_id,"
                + " m.name, m.country FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id WHERE c.id = ?"
                + " AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by ID " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE";
        List<Car> car = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from data base!", e);
        }
        for (Car cars : car) {
            cars.setDrivers(getDriversForCar(cars.getId()));
        }
        return car;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(PARAMETER_OF_FIRST_INDEX, car.getModel());
            statement.setLong(PARAMETER_OF_SECOND_INDEX, car.getManufacturer().getId());
            statement.setLong(PARAMETER_OF_THIRD_INDEX, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car: " + car, e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by ID: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, c.manufacturer_id, "
                + "m.name, m.country FROM cars c JOIN cars_drivers cd "
                + "ON cd.car_id = c.id JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ?;";
        List<Car> car = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get cars by driver ID: " + driverId, e);
        }
        for (Car cars : car) {
            cars.setDrivers(getDriversForCar(cars.getId()));
        }
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(PARAMETER_OF_SECOND_INDEX, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert car to data base for " + car, e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String carModel = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturersName = resultSet.getString("name");
        String manufacturersCountry = resultSet.getString("country");
        return new Car(carId, carModel, new Manufacturer(manufacturerId,
                manufacturersName, manufacturersCountry));
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT drivers.id, drivers.name, drivers.license_number "
                + "FROM drivers JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            if (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers by ID " + id, e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("drivers.id", Long.class);
        String driverName = resultSet.getString("drivers.name");
        String driverLicenseNumber = resultSet.getString("drivers.license_number");
        return new Driver(driverId, driverName, driverLicenseNumber);
    }

    private void deleteDrivers(Car car) {
        String query = "DELETE * FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(PARAMETER_OF_FIRST_INDEX, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers by car ID " + car.getId(), e);
        }
    }
}
