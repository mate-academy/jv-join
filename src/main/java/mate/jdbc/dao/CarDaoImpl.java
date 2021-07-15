package mate.jdbc.dao;

import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement saveCarStatement = connection.prepareStatement(createCarQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        insertCarsAndDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.model, man.name, man.country, manufacturer_id " +
                "FROM cars JOIN manufacturers man ON cars.manufacturer_id = man.id " +
                "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            if (car != null){
                car.setDrivers(getInfoAboutDrivers(id));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM drivers WHERE is_deleted = FALSE"; // CHANGE
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE drivers "
                + "SET name = ?, license_number = ? "
                + "WHERE id = ? AND is_deleted = FALSE"; // CHANGE
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement
                     = connection.prepareStatement(query)) {
           //updateCarStatement.setString(1, car.);
            //updateCarStatement.setString(2, car.);
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE drivers SET is_deleted = TRUE WHERE id = ?"; // CHANGE
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("model"));
        return car;
    }

    private void insertCarsAndDrivers(Car car) {
        String carsDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement saveCarDriverStatement = connection.prepareStatement(carsDriversQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            saveCarDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                saveCarDriverStatement.setLong(2, driver.getId());
                saveCarDriverStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
    }

    private List<Driver> getInfoAboutDrivers(Long id) {
        String getDriversQuery = "SELECT d.id, d.name, d.license_number, cd.driver_id " +
                "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id " +
                "WHERE cd.car_id = ? AND d.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversStatement = connection.prepareStatement(getDriversQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get info about drivers in car with id "
                    + id + ". ", throwable);
        }
    }
}
