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
            throw new DataProcessingException("Can't create "
                    + car + ". ", e);
        }
        addDriversForCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query =
                "SELECT cars.id, model, manufacturer_id, "
                        + "manufacturers.name, manufacturers.country "
                        + "FROM cars "
                        + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.id = ? AND cars.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setAllDriverForCar(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query =
                "SELECT cars.id, model, manufacturer_id, "
                        + "manufacturers.name, manufacturers.country "
                        + "FROM cars "
                        + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car", e);
        }
        for (Car car : cars) {
            car.setAllDriverForCar(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteDriversForCar(car.getId());
        addDriversForCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query =
                "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarsStatement =
                        connection.prepareStatement(query)) {
            deleteCarsStatement.setLong(1, id);
            return deleteCarsStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query =
                "SELECT cars_driver.car_id "
                + "FROM drivers "
                + "JOIN cars_driver ON cars_driver.driver_id = drivers.id "
                + "WHERE drivers.id = ? AND drivers.is_deleted = false";
        List<Long> carsId = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            getCarStatement.setLong(1, driverId);
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getObject("car_id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get List<Car> by driverId " + driverId, e);
        }
        List<Car> cars = new ArrayList<>();
        for (Long id : carsId) {
            Optional<Car> car = get(id);
            if (car.isPresent()) {
                cars.add(car.get());
            }
        }
        return cars;
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
            throw new DataProcessingException("Can't add driver for car " + car, e);
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

    private void deleteDriversForCar(Long carId) {
        String query = "DELETE "
                + "FROM cars_driver WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversFromCarStatement =
                        connection.prepareStatement(query)) {
            removeDriversFromCarStatement.setLong(1, carId);
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers by car_id " + carId, e);
        }
    }
}
