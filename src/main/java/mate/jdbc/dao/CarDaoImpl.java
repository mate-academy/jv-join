package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS id, "
                + "manufacturers.`name` AS manufacturer_name,  "
                + "cars.model AS model,  "
                + "manufacturers.country AS country, "
                + "manufacturers.id AS manufacturers_id "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Car car = new Car();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
            return Optional.of(car);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by ID: " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver() {
        String query = "SELECT cars.id AS id, "
                + "manufacturers.`name` AS manufacturer_name, "
                + "cars.model AS model, "
                + "manufacturers.country AS country, "
                + "manufacturers.id AS manufacturers_id "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from cars DB.",
                    throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = getAllByDriver();
        List<Car> carsByDriver = new ArrayList<>();
        for (Car car : cars) {
            long count = car.getDrivers().stream()
                    .map(Driver::getId)
                    .filter(id -> id.equals(driverId))
                    .count();
            if (count > 0) {
                carsByDriver.add(car);
            }
        }
        return carsByDriver;
    }

    @Override
    public Car update(Car car) {
        Set<Driver> carDriversInDB = getAllDriversByCarId(car.getId());
        if (!carDriversInDB.equals(car.getDrivers())) {
            removeAllDriversByCar(car);
            insertDriversIntoCar(car);
        }
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", throwable);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    private void insertDriversIntoCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarsDriversStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertCarsDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertCarsDriversStatement.setLong(2, driver.getId());
                insertCarsDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert drivers to car "
                    + car + ". ", throwable);
        }
    }

    private void removeAllDriversByCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeCarsDriversStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            removeCarsDriversStatement.setLong(1, car.getId());
            removeCarsDriversStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't remove drivers from car "
                    + car + ". ", throwable);
        }
    }

    private Set<Driver> getAllDriversByCarId(Long carId) {
        String query = "SELECT cars.id AS car_id, "
                + "drivers.id AS driver_id,  "
                + "drivers.`name` AS driver_name,  "
                + "drivers.license_number AS driver_license "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars_drivers.car_id = cars.id "
                + "JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getDriversStatement.executeQuery();
            Set<Driver> drivers = new HashSet<>();
            while (resultSet.next()) {
                Long driverCarId = resultSet.getObject("car_id", Long.class);
                Driver driver = parseDriverFromResultSet(resultSet);
                if (carId.equals(driverCarId)) {
                    drivers.add(driver);
                }
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of drivers for car with ID:"
                    + carId + " !", throwable);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("driver_id", Long.class);
        String driverName = resultSet.getString("driver_name");
        String driverLicense = resultSet.getString("driver_license");
        Driver driver = new Driver(driverName, driverLicense);
        driver.setId(driverId);
        return driver;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        String manufacturerName = resultSet.getString("manufacturer_name");
        Long manufacturerId = resultSet.getObject("manufacturers_id", Long.class);
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Long carId = resultSet.getObject("id", Long.class);
        String carModel = resultSet.getString("model");
        Set<Driver> carDrivers = getAllDriversByCarId(carId);
        Car car = new Car(carModel, manufacturer);
        car.setId(carId);
        car.setDrivers(carDrivers);
        return car;
    }
}
