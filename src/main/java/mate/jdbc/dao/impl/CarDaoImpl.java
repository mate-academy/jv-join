package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.ManufacturerService;
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
        fillCarDrivers(car, car.getDrivers());
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT id, model, manufacturer_id "
                + "FROM cars "
                + "WHERE id = ? AND is_deleted = FALSE;";
        Car car = null;
        long manufacturerId = 0L;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
                manufacturerId = resultSet.getLong("manufacturer_id");
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            fillCarComplexFields(car, manufacturerId);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        Map<Car, Long> carManufacturerMap = new HashMap<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                cars.add(car);
                carManufacturerMap.put(car, resultSet.getLong("manufacturer_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car car: cars) {
            if (car != null) {
                fillCarComplexFields(car, carManufacturerMap.get(car));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + " SET model = ?, manufacturer_id = ? "
                + " WHERE id = ? AND is_deleted = FALSE;";
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
        deleteCarsDrivers(car.getId());
        fillCarDrivers(car, car.getDrivers());
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, c.manufacturer_id "
                + " FROM cars_drivers AS cd INNER JOIN cars AS c"
                + " ON cd.car_id = c.id "
                + " WHERE c.is_deleted = FALSE"
                + " AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        Map<Car, Long> carManufacturerMap = new HashMap<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                cars.add(car);
                carManufacturerMap.put(car, resultSet.getLong("manufacturer_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from"
                    + " carsDB by drivers ID " + driverId + ".", e);
        }
        for (Car car: cars) {
            if (car != null) {
                fillCarComplexFields(car, carManufacturerMap.get(car));
            }
        }
        return cars;
    }

    public void fillCarDrivers(Car car, List<Driver> drivers) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : drivers) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't fill table cars_drivers for car "
                    + car + " and drivers " + drivers + ". ", e);
        }
    }

    public void deleteCarsDrivers(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete records "
                    + "from table cars_drivers for car ID " + carId + ". ", e);
        }
    }

    private void fillCarComplexFields(Car car, Long manufacturerId) {
        car.setManufacturer(getManufacturer(manufacturerId));
        car.setDrivers(getDriverListByCarId(car.getId()));
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String model = resultSet.getString("model");
        return new Car(id, model);
    }

    private Manufacturer getManufacturer(Long manufacturerId) {
        Injector injector = Injector.getInstance("mate.jdbc");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        return manufacturerService.get(manufacturerId);
    }

    private List<Driver> getDriverListByCarId(Long carId) {
        String query = "SELECT d.id, d.name, d.license_number FROM cars_drivers AS cd "
                + " INNER JOIN drivers AS d "
                + " ON cd.driver_id = d.id"
                + " WHERE car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list"
                    + " of drivers from cars_drivers table.", e);
        }
    }
}
