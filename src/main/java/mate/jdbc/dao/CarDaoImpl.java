package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarRequest = "INSERT into cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(createCarRequest,
                    Statement.RETURN_GENERATED_KEYS)) {
        statement.setLong(1, car.getManufacturer().getId());
        statement.setString(2, car.getModel());
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        if (resultSet.next()) {
            car.setId(resultSet.getObject(1, Long.class));
        }
        addRecordToCarsDriversTable(car);
        return car;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest =
                "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id " +
                "FROM cars c " +
                "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                "WHERE TRUE " +
                "AND c.id = ? " +
                "AND c.is_deleted = 0 ";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getCarRequest,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car.setId(id);
                car.setModel(resultSet.getString("model"));
                car.setManufacturer(getManufacturerForCar(resultSet.getObject("manufacturer_id", Long.class)));
                car.setDrivers(getDriversForCar(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(car);
    }


    @Override
    public List<Car> getAll() {
        String getAllCarsRequest =
                "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id " +
                "FROM cars c " +
                "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                "WHERE TRUE " +
                "AND c.is_deleted = 0 ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllCarsRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private void addRecordToCarsDriversTable(Car car) {
        String queryForJoiningTable = "INSERT into cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryForJoiningTable,
                     Statement.RETURN_GENERATED_KEYS)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Manufacturer getManufacturerForCar(Long id) {
        String queryForJoiningTable =
                "SELECT m.id, m.name, m.country " +
                "FROM manufacturers m " +
                "JOIN cars c ON c.manufacturer_id = m.id " +
                "WHERE TRUE " +
                "AND c.id = ? " +
                "AND m.is_deleted = 0 ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getManufacturerStatement = connection.prepareStatement(queryForJoiningTable,
                     Statement.RETURN_GENERATED_KEYS)) {
            getManufacturerStatement.setLong(1, id);
            ResultSet resultSet = getManufacturerStatement.executeQuery();
            Manufacturer manufacturer = new Manufacturer();
            if (resultSet.next()) {
                manufacturer.setId(resultSet.getObject("id", Long.class));
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
            }
            return manufacturer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getAllDriversForCarRequest =
                "SELECT id, name, license_number " +
                "FROM drivers d " +
                "JOIN cars_drivers cd ON d.id = cd.driver_id " +
                "WHERE TRUE " +
                "AND cd.car_id = ? " +
                "AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllDriversStatement = connection.prepareStatement(getAllDriversForCarRequest,
                    Statement.RETURN_GENERATED_KEYS)) {
            getAllDriversStatement.setLong(1, id);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        String car_id = "car_id";
        Manufacturer manufacturer = getManufacturerForCar(resultSet.getLong(car_id));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }
}
