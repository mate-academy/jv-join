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
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
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
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        addDriversForCar(car);
        return car;
    }

    private void addDriversForCar(Car car) {
        String query = "INSERT INTO cars_driver (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverForCarStatement = connection.prepareStatement(query)) {
            for (Driver driver : car.getAllDriverForCar()) {
                addDriverForCarStatement.setLong(1, driver.getId());
                addDriverForCarStatement.setLong(2, car.getId());
                addDriverForCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert driver for car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest =
                "SELECT cars.id, model, manufacturer_id, "
                        + "manufacturers.name, manufacturers.country "
                        + "FROM cars "
                        + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.id = ? AND cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCarWithResultSet(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
    }

    private Car getCarWithResultSet(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        Car car = new Car();
        car.setId(carId);
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setAllDriverForCar(getDriversForCar(carId));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers "
                + "JOIN cars_driver ON drivers.id = cars_driver.driver_id "
                + "WHERE cars_driver.car_id = ? AND drivers.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDrivers(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by id "
                    + carId, e);
        }
    }

    private Driver parseDrivers(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    @Override
    public List<Car> getAll() {
        String getRequest =
                "SELECT cars.id, model, manufacturer_id, "
                        + "manufacturers.name, manufacturers.country "
                        + "FROM cars "
                        + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getRequest)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCarWithResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car", e);
        }
    }

    @Override
    public Car update(Car manufacturer) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest =
                "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarsStatement =
                        connection.prepareStatement(deleteRequest)) {
            deleteCarsStatement.setLong(1, id);
            return deleteCarsStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }
}
