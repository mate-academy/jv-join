package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertAllRelationsCarWithDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, model, manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
                car.setManufacturer(parseManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, "
                + "model, manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet getAllResult = getAllStatement.executeQuery();
            while (getAllResult.next()) {
                Car car = parseCar(getAllResult);
                car.setManufacturer(parseManufacturer(getAllResult));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get all cars from db.", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car)));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, "
                + "manufacturer_id = ? WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteAllRelationsCarWithDrivers(car.getId());
        insertAllRelationsCarWithDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String query = "SELECT c.id as car_id, model, manufacturer_id, name, country "
                + "FROM cars_drivers cd JOIN cars c on c.id = cd.car_id "
                + "    JOIN manufacturers m on m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = false AND cd.driver_id = ?";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            getAllStatement.setLong(1, id);
            ResultSet getAllResult = getAllStatement.executeQuery();
            while (getAllResult.next()) {
                Car car = parseCar(getAllResult);
                car.setManufacturer(parseManufacturer(getAllResult));
                allCars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car by driver_id " + id, e);
        }
        allCars.forEach(car -> car.setDrivers(getDriversForCar(car)));
        return allCars;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("c.id"));
        car.setModel(resultSet.getString("model"));
        return car;
    }

    private Driver parseDriver(ResultSet resultDrivers) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultDrivers.getLong("d.id"));
        driver.setName(resultDrivers.getString("name"));
        driver.setLicenseNumber(resultDrivers.getString("license_number"));
        return driver;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private int deleteAllRelationsCarWithDrivers(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement
                        = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, carId);
            return deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t deleted relations between drivers and car, "
                    + "by car_id = " + carId, e);
        }
    }

    private boolean insertAllRelationsCarWithDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(query)) {
            insertStatement.setLong(1, car.getId());
            int numOfUpdatedLines = 0;
            List<Driver> drivers = car.getDrivers();
            for (Driver driver : drivers) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
                numOfUpdatedLines++;
            }
            return numOfUpdatedLines == drivers.size();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert relation cars with drivers. "
                    + car, e);
        }
    }

    private List<Driver> getDriversForCar(Car car) {
        String query = "SELECT d.id, name, license_number FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = false AND cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, car.getId());
            ResultSet resultDrivers = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultDrivers.next()) {
                drivers.add(parseDriver(resultDrivers));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all drivers for car_id = "
                    + car.getId(), e);
        }

    }
}
