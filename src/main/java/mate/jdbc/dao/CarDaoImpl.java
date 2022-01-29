package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        createCarsDriversByCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id car_id, c.model car_model, " +
                "m.id man_id, m.country man_country, m.name man_name," +
                "FROM cars c JOIN manufacturers m ON m.id = c.manufacturer_id " +
                "WHERE c.id = ? AND c.is_deleted = 0";
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            car = getCar(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        return Optional.of(getCarWithDrivers(car));
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT id, manufacturer_id, model FROM cars WHERE is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection()) {

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private void createCarsDriversByCar(Car car) {
        car.getDrivers().stream()
                .forEach(driver -> createCarDriverRecord(car, driver));
    }

    private void createCarDriverRecord(Car car, Driver driver) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, driver.getId());
            statement.setObject(2, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create reference driver->car for"
                    + driver + " AND " + car + ". ", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("car_id"));
        car.setModel(resultSet.getString("car_model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("man_id"));
        manufacturer.setCountry(resultSet.getString("man_country"));
        manufacturer.setName(resultSet.getString("man_name"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Car getCarWithDrivers(Car car) {
        String query = "SELECT driver_id FROM cars_drivers WHERE car_id = ?";
        List<Long> driverIdList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driverIdList.add(resultSet.getLong("driver_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Error getting drivers data for car. ", e);
        }
        List<Driver> drivers = driverIdList.stream()
                .map(driverDao::get)
                .map(Optional::get)
                .collect(Collectors.toList());
        car.setDrivers(drivers);
        return car;
    }

}
