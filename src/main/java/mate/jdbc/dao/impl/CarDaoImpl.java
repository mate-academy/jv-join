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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Dao
public class CarDaoImpl implements CarDao {
    private static final Logger log = LogManager.getLogger(CarDaoImpl.class);

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            log.info("{} was created", car);
            return car;
        } catch (SQLException e) {
            log.error("Unable to create {}, DataProcessingException {}", car, e);
            throw new DataProcessingException("Couldn't create car. " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.model, "
                + "c.manufacturer_id, m.name, m.country\n"
                + "FROM cars c\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.model, c.manufacturer_id, m.name, m.country\n"
                + "FROM cars c\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id\n"
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars "
                    + "from cars table.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            log.info("Element {}, was updated", car);
            return car;
        } catch (SQLException e) {
            log.error("Unable to update {}, DataProcessingException {}", car, e);
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            log.info("Element with id {}, was deleted", id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Unable to delete car with ID {}, "
                    + "DataProcessingException {}", id, e);
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        List<Driver> drivers = getDrivers(carId);
        return new Car(carId, model, manufacturer, drivers);
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private List<Driver> getDrivers(Long id) throws SQLException {
        String getAllDrivers = "SELECT d.id as driver_id, d.name, d.license_number, c.model\n"
                + "FROM drivers d\n"
                + "JOIN cars_drivers cd on d.id = cd.driver_id\n"
                + "JOIN cars c ON cd.car_id = c.id\n"
                + "WHERE cd.car_id = ? ";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDrivers)) {
            statement.setLong(1, id);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers "
                    + "from drivers table.", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String sqlGetAllDrivers = "SELECT c.id as car_id, c.model,\n"
                + "       m.id as manufacturer_id, m.name, m.country,\n"
                + "       d.id as driver_id, d.name as driver_name, d.license_number\n"
                + "FROM cars c\n"
                + "JOIN manufacturers m on m.id = c.manufacturer_id\n"
                + "JOIN cars_drivers cd on c.id = cd.car_id\n"
                + "JOIN drivers d on d.id = cd.driver_id\n"
                + "WHERE d.id = ? AND c.is_deleted = FALSE";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(sqlGetAllDrivers)) {
            statement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getAllCarByDriverId(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars "
                    + "from cars table.", e);
        }
    }

    private Car getAllCarByDriverId(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        List<Driver> drivers = new ArrayList<>();
        while (resultSet.next()) {
            drivers.add(
                    new Driver(
                    resultSet.getObject("driver_id", Long.class),
                    resultSet.getString("driver_name"),
                    resultSet.getString("license_number")
                    )
            );
        }
        return new Car(carId, model, manufacturer, drivers);
    }
}
