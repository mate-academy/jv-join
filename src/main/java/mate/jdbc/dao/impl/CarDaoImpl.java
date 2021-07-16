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
            deleteFromCarsDrivers(car);
            insertToCarsDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, "
                + " c.name AS car_name, "
                + " m.id AS manufacturer_id, m.country, "
                + " m.name AS manufacturer_name, m.country "
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + " WHERE c.id = ? ;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = setCarFromResult(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car id = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
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
                Car car = setCarFromResult(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + cars, e);
        }
        for (Car car : cars) {
            getDriversForCar(car.getId());
        }
        return cars;
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
        insertToCarsDrivers(car);
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
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT car_id "
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
            cars.add(get(id).get());
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

    private void insertToCarsDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?,?) ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateElementStatement = connection.prepareStatement(query)) {
            updateElementStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                updateElementStatement.setLong(2, driver.getId());
                updateElementStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't car_drivers  with car " + car, throwable);
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
            while (resultSet.next()) {
                Driver driver = new Driver();
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

    private Car setCarFromResult(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setId(resultSet.getObject("car_id",Long.class));
        car.setName(resultSet.getString("car_name"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
