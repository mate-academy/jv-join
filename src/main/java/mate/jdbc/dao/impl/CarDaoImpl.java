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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
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
            throw new DataProcessingException("Couldn't create car " + car + ".", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model AS car_model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model AS car_model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + " in cars DB.", e);
        }
        deleteDriversFromCarById(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car from DB with id: " + id, e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model AS car_model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers  cd ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE AND driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCar(car.getId()));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("car_model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        return new Car(id, model, manufacturer);
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        return new Manufacturer(id, name, country);
    }

    private List<Driver> getAllDriversByCar(Long carId) {
        String query = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers by car id " + carId, e);
        }
        return drivers;
    }

    private void deleteDriversFromCarById(Long carId) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers "
                    + "from car with id: " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void insertDrivers(Car car) {
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers "
                    + "to car with id: " + car, e);
        }
    }
}
