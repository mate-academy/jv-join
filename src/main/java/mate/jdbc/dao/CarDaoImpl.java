package mate.jdbc.dao;

import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (name, number) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement saveCarStatement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setObject(1, car.getDrivers());
            saveCarStatement.setObject(2, car.getManufacturer());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT id, driver, manufacturer "
                + "FROM cars "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCasStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCasStatement.executeQuery();
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
        String query = "UPDATE cas "
                + "SET driver = ?, manufacturer = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateDriverStatement
                     = connection.prepareStatement(query)) {
            updateDriverStatement.setObject(1, car.getDrivers());
            updateDriverStatement.setObject(2, car.getManufacturer());
            updateDriverStatement.setLong(3, car.getId());
            updateDriverStatement.executeUpdate();
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars JOIN drivers ON cars.did = drivers.id WHERE deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCasStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCasStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars by driver from carsDB.",
                    throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("car_id", Long.class);
        List<Driver> drivers = (List<Driver>) resultSet.getObject("car_driver");
        Manufacturer manufacturer = (Manufacturer) resultSet.getObject("car_manufacturer");
        Car car = new Car(drivers, manufacturer);
        car.setId(newId);
        return car;
    }
}
