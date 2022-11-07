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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
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
        String query = "SELECT cars.id FROM cars WHERE is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            Car car;
            while (resultSet.next()) {
                car = get(resultSet.getObject(1, Long.class)).get();
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE is_deleted = FALSE AND cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            Car car;
            while (resultSet.next()) {
                car = get(resultSet.getObject(1, Long.class)).get();
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driverId: " + driverId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                            = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            if (!car.getDrivers().isEmpty()) {
                updateCarsDrivers(car);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName("name");
        manufacturer.setCountry("country");
        car.setManufacturer(manufacturer);
        if (!getAllDrivers(car.getId()).isEmpty()) {
            car.setDrivers(getAllDrivers(car.getId()));
        }
        return car;
    }

    private List<Driver> getAllDrivers(Long id) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void updateCarsDrivers(Car car) {
        deleteCarsDriversWithCarId(car.getId());
        createConnectionOfDriversWithCar(car.getId(), car.getDrivers());
    }

    private boolean deleteCarsDriversWithCarId(Long id) {
        String query = "DELETE FROM cars_drivers cd WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers from car with id " + id, e);
        }
    }

    private void createConnectionOfDriversWithCar(Long id, List<Driver> drivers) {
        String query = "INSERT INTO cars_drivers SET car_id = ?, driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            for (Driver driver: drivers) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create connections in cars_driversDB "
                    + id, e);
        }
    }
}
