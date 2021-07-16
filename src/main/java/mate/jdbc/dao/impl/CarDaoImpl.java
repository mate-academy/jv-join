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
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car, throwable);
        }
        return createConnectionBetweenCarAndDriver(car);
    }

    private Car createConnectionBetweenCarAndDriver(Car car) {
        String connectionQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement connectCarToDriverStatement =
                        connection.prepareStatement(connectionQuery)) {
            connectCarToDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                connectCarToDriverStatement.setLong(2, driver.getId());
                connectCarToDriverStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create connection between " + car
                    + " and " + car.getDrivers(), throwable);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id, model, manufacturer_id, name, country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id=m.id "
                + "WHERE c.is_deleted=false AND c.id=?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = createCarWithoutDrivers(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car", throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id, model, manufacturer_id, name, country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id=m.id "
                + "WHERE c.is_deleted=false;";
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            cars = new ArrayList<>();
            while (resultSet.next()) {
                Car car = createCarWithoutDrivers(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of cars", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car createCarWithoutDrivers(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        return new Car(carId, model, manufacturer, null);
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversQuery = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id=cd.driver_id "
                + "WHERE is_deleted=false AND cd.car_id=?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement =
                        connection.prepareStatement(getDriversQuery)) {
            getDriverStatement.setLong(1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of drivers", throwable);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Long> carIdFromDriver = getCarIdFromDriver(driverId);
        List<Car> carsDrivenByDriver = new ArrayList<>();
        for (Long id : carIdFromDriver) {
            carsDrivenByDriver.add(get(id).orElseThrow(() -> new DataProcessingException(
                    "Could not get car id for driver with id " + driverId)));
        }
        return carsDrivenByDriver;
    }

    private List<Long> getCarIdFromDriver(Long driverId) {
        String getCarIdQuery = "SELECT car_id FROM drivers d "
                + "JOIN cars_drivers cd ON d.id=cd.driver_id "
                + "WHERE is_deleted=false AND d.id=?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarIdStatement = connection.prepareStatement(getCarIdQuery)) {
            getCarIdStatement.setLong(1, driverId);
            ResultSet resultSet = getCarIdStatement.executeQuery();
            List<Long> driverCarIds = new ArrayList<>();
            while (resultSet.next()) {
                driverCarIds.add(resultSet.getObject("car_id", Long.class));
            }
            return driverCarIds;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of car id", throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model=?, manufacturer_id=? "
                + "WHERE id=? AND is_deleted=false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update " + car, throwable);
        }
        deleteConnectionBetweenCarAndDriver(car);
        createConnectionBetweenCarAndDriver(car);
        return car;
    }

    private Car deleteConnectionBetweenCarAndDriver(Car car) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id=?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete cars drivers for " + car, throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted=true WHERE id=?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car by id " + id, throwable);
        }
    }
}
