package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Dao
public class CarDaoImpl implements CarDao {
    private static final Logger logger = LogManager.getLogger(CarDaoImpl.class);

    @Override
    public Car create(Car car) {
        logger.info("Method create was called with car " + car);
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't create car " + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        logger.info("Method get was called with id " + id);
        String query = "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get driver by id " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false;";
        Car car = null;
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get all cars from carsDB", throwables);
        }
        for (Car automobile : cars) {
            automobile.setDrivers(getDriversForCar(automobile.getId()));
        }
        logger.info("The all data from DB was successful fetched");
        return cars;
    }

    @Override
    public Car update(Car car) {
        logger.info("Method update was called with driver: " + car);
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't update car " + car, throwables);
        }
        deleteAllRelationsFromCarsDrivers(car);
        insertDrivers(car);
        logger.info("Update data of car was successful");
        return car;
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Method delete was called with id: " + id);
        String query = " UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwables);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        logger.info("Method getAllByDriver was called with id: " + driverId);
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars JOIN manufacturers AS m ON cars.manufacturer_id = m.id "
                + "JOIN cars_drivers AS cd ON cars.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND cars.is_deleted = false;";
        Car car = null;
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get cars by driverId "
                    + driverId, throwables);
        }
        logger.info("The all data about cars by driver id " + driverId
                + " was successful fetched from DB");
        return cars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversRequest = "SELECT id, name, license_number "
                + "FROM drivers AS d JOIN cars_drivers AS cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDriversRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get all driver by car carId "
                    + carId, throwables);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't add drivers to car " + car, throwables);
        }
    }

    private void deleteAllRelationsFromCarsDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't delete relations "
                    + "from cars_drivers by car " + car, throwables);
        }
    }
}
