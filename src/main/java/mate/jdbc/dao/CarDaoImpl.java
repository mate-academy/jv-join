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
    private static final int INDEX_COLUMN_ONE = 1;
    private static final int INDEX_COLUMN_TWO = 2;

    @Override
    public Car create(Car car) {
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(INDEX_COLUMN_ONE, car.getModel());
            createCarStatement.setLong(INDEX_COLUMN_TWO, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(INDEX_COLUMN_ONE, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB. Car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                    connection.prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(INDEX_COLUMN_ONE, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(INDEX_COLUMN_TWO, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car. Car: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id as car_id, c.model as car_model, m.id as" +
                " manufacturer_id, m.name as manufacturer_name, m.country as " +
                "manufacturer_country FROM cars c JOIN manufacturers m ON " +
                "c.manufacturer_id = m.id  where c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                    connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(INDEX_COLUMN_ONE, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllRequest = "SELECT c.id as car_id, c.model as car_model, m.id as" +
                " manufacturer_id, m.name as manufacturer_name, m.country as " +
                "manufacturer_country FROM cars c JOIN manufacturers m ON " +
                "c.manufacturer_id = m.id where c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                     connection.prepareStatement(selectAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all data from cars table", e);
        }
        return cars;
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
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long CarId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers d JOIN" +
                " cars_drivers cd ON d.id = cd.driver_id where cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllDriversStatement =
                     connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(INDEX_COLUMN_ONE, CarId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find any drivers by car_id: " + CarId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
