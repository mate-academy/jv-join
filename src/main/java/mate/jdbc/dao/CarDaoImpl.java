package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCar =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            createCar.setString(1,car.getModel());
            createCar.setLong(2, car.getManufacturer().getId());
            createCar.executeUpdate();
            ResultSet generatedKeys = createCar.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriver = connection.prepareStatement(query)) {
            addDriver.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriver.setLong(2, driver.getId());
                addDriver.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, m.name, m.country " +
                "FROM cars c " +
                "JOIN manufacturers m " +
                "ON c.manufacturer_id = m.id " +
                "WHERE c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCar = connection.prepareStatement(query)) {
            getCar.setLong(1, id);
            ResultSet resultSet = getCar.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(car != null) {
            car.setDrivers(getDriver(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
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
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriver(Long id) {
        String query = "SELECT id, name, license_number " +
                "FROM taxi_db.drivers d " +
                "JOIN cars_drivers cd " +
                "ON d.id = cd.driver_id " +
                "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement getAllDrivers = connection.prepareStatement(query)){

            getAllDrivers.setLong(1, id);
            ResultSet resultSet = getAllDrivers.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }
}
