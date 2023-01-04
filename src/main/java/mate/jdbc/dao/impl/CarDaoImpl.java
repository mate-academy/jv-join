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
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: " + car, e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, model, name, country, manufacturer_id "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, model, name, country, manufacturer_id "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of all cars", e);
        }
        cars.forEach(
                car -> car.setDrivers(
                        getDriversByCarId(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    String.format("Couldn't update car: %s in cars DB.", car), e);
        }
        removeDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, model, name, country, manufacturer_id "
                + "FROM cars c JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = false AND cd.is_deleted = false;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                carList.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get list of all cars with driver id = " + driverId, e);
        }
        carList.forEach(
                car -> car.setDrivers(
                        getDriversByCarId(car.getId())));
        return carList;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, name, country);
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        return new Car(carId, model, manufacturer, null);
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE car_id = ? AND d.is_deleted = false AND cd.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get list of drivers by car id = " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void removeDriversFromCar(Car car) {
        String query = "UPDATE cars_drivers SET is_deleted = true WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete relations in cars_drivers for car: " + car, e);
        }
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't create new relations in cars_drivers for car: " + car, e);
        }
    }
}
