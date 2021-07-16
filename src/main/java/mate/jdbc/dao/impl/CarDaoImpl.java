package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String query = "INSERT INTO cars (name, manufacturer_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getName());
            createCarStatement.setObject(2, car.getManufacturer());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id AS car_id, "
                + " c.name AS car_name, "
                + " m.id AS manufacturer_id, m.country, "
                + " m.name AS manufacturer_name, m.country "
                + " FROM taxi_my.cars c JOIN taxi_my.manufacturers m ON c.manufacturer_id = m.id "
                + " WHERE c.id = ? ;";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
                manufacturer.setName(resultSet.getString("manufacturer_name"));
                manufacturer.setCountry(resultSet.getString("country"));
                car.setId(id);
                car.setName(resultSet.getString("car_name"));
                car.setManufacturer(manufacturer);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car id = " + id, e);
        }
        car.setDrivers(getDriversForCar(car.getId()));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS 'car_id',"
                + " c.name AS 'car_name',"
                + " m.id AS 'manufacturer_id',"
                + " m.name AS 'manufacturer_name',"
                + " m.country AS 'manufacturer_country'"
                + " FROM CARS c JOIN MANUFACTURERS m on c.manufacturer_id = m.id "
                + " WHERE c.id = 1;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
                manufacturer.setName(resultSet.getString("manufacturer_name"));
                manufacturer.setCountry(resultSet.getString("manufacturer_country"));
                Car car = new Car();
                car.setId(resultSet.getObject("car_id", Long.class));
                car.setName(resultSet.getString("car_name"));
                car.setManufacturer(manufacturer);
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + cars, e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = " UPDATE cars SET name = ?, manufacturer_id = ? "
                + " WHERE id = ? AND is_deleted = FALSE; ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(query)) {
            createCarStatement.setString(1, car.getName());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.setObject(3, car.getId());
            createCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car " + car, e);
        }
        deleteFromCarsDrivers(car);
        updateFromCarsDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriverStatement = connection.prepareStatement(query)) {
            deleteDriverStatement.setLong(1, id);
            return deleteDriverStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        car.getDrivers().add(driver);
        update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().remove(driver);
        update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * "
                + " FROM cars_drivers cd "
                + " JOIN drivers d on cd.driver_id = d.id "
                + " WHERE d.id = ? AND d.is_deleted = false;";
        List<Long> idCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteElementStatement = connection.prepareStatement(query)) {
            deleteElementStatement.setLong(1, driverId);
            ResultSet resultSet = deleteElementStatement.executeQuery();
            while (resultSet.next()) {
                idCars.add(resultSet.getObject("car_id", Long.class));
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't getAll car with id " + driverId, throwable);
        }
        List<Car> cars = new ArrayList<>();
        for (Long id : idCars) {
            cars.add(get(id));
        }
        return cars;
    }

    private void deleteFromCarsDrivers(Car car) {
        String query = "DELETE * FROM cars_drivers WHERE car_id = ? ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteElementStatement = connection.prepareStatement(query)) {
            deleteElementStatement.setLong(1, car.getId());
            deleteElementStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with car " + car, throwable);
        }
    }

    private void updateFromCarsDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?,?) ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateElementStatement = connection.prepareStatement(query)) {
            updateElementStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                updateElementStatement.setLong(2, driver.getId());
                updateElementStatement.executeUpdate();
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't drivers with car " + car, throwable);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers d "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getElementStatement = connection.prepareStatement(query)) {
            getElementStatement.setLong(1, carId);
            ResultSet resultSet = getElementStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            Driver driver = new Driver();
            while (resultSet.next()) {
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get driver for car by id "
                    + carId, throwable);
        }
    }
}
