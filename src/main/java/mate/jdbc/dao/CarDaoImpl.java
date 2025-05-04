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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        createCarsDriversByCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id car_id, c.model car_model, "
                + "m.id man_id, m.country man_country, m.name man_name "
                + "FROM cars c JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = 0";
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            car = getCar(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        return Optional.of(getCarWithDrivers(car));
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id car_id, c.model car_model, "
                + "m.id man_id, m.country man_country, m.name man_name "
                + "FROM cars c JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = 0";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        cars.forEach(this::getCarWithDrivers);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car " + car, e);
        }
        clearDriversFromCar(car.getId());
        createCarsDriversByCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        clearDriversFromCar(id);
        String query = "UPDATE cars SET is_deleted = 1 WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car (id: " + id + ")", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id car_id, c.model car_model, "
                + "m.id man_id, m.country man_country, m.name man_name "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = 0";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        cars.forEach(this::getCarWithDrivers);
        return cars;
    }

    private void clearDriversFromCar(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers from cars_drivers "
                    + "for car_id: " + carId, e);
        }
    }

    private void createCarsDriversByCar(Car car) {
        if (car.getDrivers() == null) {
            return;
        }
        car.getDrivers().stream()
                .forEach(driver -> createCarDriverRecord(driver, car));
    }

    private void createCarDriverRecord(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, driver.getId());
            statement.setObject(2, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create reference driver->car for"
                    + driver + " AND " + car + ". ", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("car_id"));
        car.setModel(resultSet.getString("car_model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("man_id"));
        manufacturer.setCountry(resultSet.getString("man_country"));
        manufacturer.setName(resultSet.getString("man_name"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Car getCarWithDrivers(Car car) {
        String query = "SELECT d.id d_id, d.name d_name, d.license_number d_license "
                + "FROM cars_drivers cd "
                + "JOIN drivers d ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = 0 "
                + "ORDER BY d_id";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getLong("d_id"));
                driver.setName(resultSet.getString("d_name"));
                driver.setLicenseNumber(resultSet.getString("d_license"));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Error getting drivers data for car. ", e);
        }
        car.setDrivers(drivers);
        return car;
    }

}
