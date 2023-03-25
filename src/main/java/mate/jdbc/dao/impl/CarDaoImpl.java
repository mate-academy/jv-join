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
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, mn.id, mn.name, mn.country  "
                + "FROM cars c JOIN manufacturers mn ON mn.id = c.manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT DISTINCT c.id, c.model, mn.id, mn.name, mn.country "
                + "FROM cars c JOIN manufacturers mn ON mn.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        if (!cars.isEmpty()) {
            for (Car car: cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteDriversFromCarsDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        boolean executeUpdate = false;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            executeUpdate = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
        if (executeUpdate) {
            deleteDriversFromCarsDrivers(id);
        }
        return executeUpdate;

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT DISTINCT c.id, c.model, mn.id, mn.name, mn.country "
                + "FROM cars c "
                + "JOIN cars_drivers cd ON cd.car_id = c.id "
                + "JOIN manufacturers mn ON mn.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE AND cd.is_deleted = FALSE "
                + "ORDER BY c.id ASC;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB, "
                    + "by driverId: " + driverId, e);
        }
        if (!cars.isEmpty()) {
            for (Car car: cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    private void deleteDriversFromCarsDrivers(Long id) {
        String query = "UPDATE cars_drivers SET is_deleted = TRUE WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars_drivers with id " + id, e);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver: car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create cars_drivers for "
                    + car + ". ", e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND cd.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car by id " + id, e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("d.id", Long.class);
        String name = resultSet.getString("d.name");
        String licenseNumber = resultSet.getString("d.license_number");
        return new Driver(id, name, licenseNumber);
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("c.id", Long.class);
        String model = resultSet.getString("c.model");
        Long manufacturerId = resultSet.getObject("mn.id", Long.class);
        String name = resultSet.getString("mn.name");
        String county = resultSet.getString("mn.country");
        return new Car(carId, model, new Manufacturer(manufacturerId, name, county));
    }
}
