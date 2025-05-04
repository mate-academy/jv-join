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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                setDrivers(car);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = new Car();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
                car.setDrivers(getDrivers(id));
            }
            return Optional.of(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get by id driver. Id:  " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update driver by car in DB. Car: " + car, e);
        }
        deleteDriversOfCar(car);
        setDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car "
                    + "with id from DB. Id:  " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id as id, manufacturer_id, model, name, country FROM cars c"
                + " JOIN cars_drivers cd ON c.id = cd.car_id JOIN manufacturers m"
                + " ON manufacturer_id = m.id WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection
                         .prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find any car from DB by driverId = "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    private void setDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to DB.", e);
        }
    }

    private List<Driver> getDrivers(Long carId) {
        String queryForDrivers = "SELECT name, license_number, driver_id FROM drivers d "
                + "INNER JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(queryForDrivers)) {
            statement.setObject(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                driver.setId(resultSet.getObject("driver_id", Long.class));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find driver by id from DB. Id: " + carId, e);
        }
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void deleteDriversOfCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers of car in DB" + car, e);
        }
    }
}
