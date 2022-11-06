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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create the car - " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection.prepareStatement(query)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into the car - " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCar = connection.prepareStatement(query)) {
            getCar.setLong(1,id);
            ResultSet resultSet = getCar.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car. ID - " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT * FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND cars_drivers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCar = connection.prepareStatement(query)) {
            getDriversForCar.setLong(1, carId);
            ResultSet resultSet = getDriversForCar.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get the drivers for a car. ID - " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturer(resultSet);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers from library_db.drivers", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCar = connection.prepareStatement(query)) {
            updateCar.setLong(1, car.getManufacturer().getId());
            updateCar.setString(2, car.getModel());
            updateCar.setLong(3, car.getId());
            updateCar.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update the car: " + car, e);
        }
        deletePreviousRelations(car);
        insertDrivers(car);
        return car;
    }

    private void deletePreviousRelations(Car car) {
        String query = "UPDATE cars_drivers SET is_deleted = TRUE WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete relations: carId - " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car: ID - " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * "
                + "FROM cars_drivers "
                + "WHERE driver_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement = connection.prepareStatement(query)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                Optional<Car> car = get(resultSet.getObject("car_id", Long.class));
                cars.add(car.get());
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get the cars for driverId: " + driverId, e);
        }
    }
}
