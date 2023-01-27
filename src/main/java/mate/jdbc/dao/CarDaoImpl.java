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
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?)";
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS car_id, cars.model, "
                + "manufacturers.id AS manufacturer_id, "
                + "manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
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
            car.setDrivers(getDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, m.id AS manufacturer_id,"
                + " m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "AND c.is_deleted = FALSE;";;
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB", e);
        }
        cars.forEach(car -> car.setDrivers(getDrivers(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.setObject(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't upgrade "
                    + car + " in carsDB.", e);
        }
        removeDriversFromCar(car.getId());
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        boolean result;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
        removeDriversFromCar(id);
        return result;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, cars.model, manufacturers.id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers from carsDB", e);
        }
        cars.forEach(car -> car.setDrivers(getDrivers(driverId)));
        return cars;
    }

    private List<Driver> getDrivers(Long driverId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT id, name, license_number FROM driver "
                + "JOIN cars_driver ON driver.id = cars_driver.driver_id "
                + "WHERE id = ? AND driver.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                Long id = resultSet.getObject("id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                driver.setId(id);
                driver.setName(name);
                driver.setLicenseNumber(licenseNumber);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers from DB by id " + driverId, e);
        }
        return drivers;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        Car car = new Car();
        Long id = resultSet.getObject("car_id", Long.class);
        car.setId(id);
        String model = resultSet.getString("model");
        car.setModel(model);
        String name = resultSet.getString("manufacturer_name");
        manufacturer.setName(name);
        String country = resultSet.getString("manufacturer_country");
        manufacturer.setCountry(country);
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        manufacturer.setId(manufacturerId);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_driver (driver_id, car_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers in cars_driver for car " + car);
        }
    }

    private void removeDriversFromCar(Long carId) {
        String query = "UPDATE cars_driver SET is_deleted = true WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove driver from DB");
        }
    }
}
