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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car, e);
        }
        addDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? and cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllCarsStatement = connection.createStatement()) {
            ResultSet resultSet = getAllCarsStatement.executeQuery(query);
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't found cars into DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET cars.model = ?, cars.manufacturer_id = ? "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car + " in DB", e);
        }
        removeDrivers(car);
        addDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET cars.is_deleted = TRUE WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car id: " + id, e);
        }
    }

    @Override
    public List<Optional<Car>> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ?;";
        List<Optional<Car>> allCarsByDriver = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarByDriver = connection.prepareStatement(query)) {
            getAllCarByDriver.setLong(1, driverId);
            ResultSet resultSet = getAllCarByDriver.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getObject("id", Long.class);
                allCarsByDriver.add(get(carId));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't found cars for driver_id: " + driverId, e);
        }
        return allCarsByDriver;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        return new Car(id, model, manufacturer);
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT * FROM drivers JOIN cars_drivers "
                + "ON cars_drivers.driver_id = drivers.id WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement driversStatement = connection.prepareStatement(query)) {
            driversStatement.setLong(1, carId);
            ResultSet resultSet = driversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by the carId: " + carId, e);
        }
    }

    private void addDrivers(Car car) {
        List<Driver> drivers = car.getDrivers();
        if (drivers.size() == 0) {
            return;
        }
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverStatement = connection.prepareStatement(query)) {
            for (Driver driver : drivers) {
                addDriverStatement.setLong(1, car.getId());
                addDriverStatement.setLong(2, driver.getId());
            }
            addDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers to car: " + car, e);
        }
    }

    private void removeDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriverFromCar = connection.prepareStatement(query)) {
            deleteDriverFromCar.setLong(1, car.getId());
            deleteDriverFromCar.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove driver from car: " + car, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver();
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }
}
