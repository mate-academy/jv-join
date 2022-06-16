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
        String createQuery = "INSERT INTO cars (manufacturer_id, model) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement
                         = connection.prepareStatement(createQuery,
                         Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                addDriversToCar(connection, car);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT cars.id, cars.model, "
                + "manufacturers.id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id  "
                + "WHERE cars.id = ? "
                + "AND cars.is_deleted = FALSE "
                + "AND manufacturers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                List<Driver> drivers = getDriversByCarId(connection, id);
                car = getCar(resultSet, drivers);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT cars.id, cars.model, "
                + "manufacturers.id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id  "
                + "WHERE cars.is_deleted = FALSE "
                + "AND manufacturers.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                List<Driver> drivers = getDriversByCarId(connection,
                        resultSet.getObject(1, Long.class));
                cars.add(getCar(resultSet, drivers));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(updateQuery)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            removeDriversFromCar(connection, car.getId());
            addDriversToCar(connection, car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarByDriverQuery = ""
                + "SELECT cars.id, cars.model, "
                + "manufacturers.id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id  "
                + "WHERE cars.is_deleted = FALSE "
                + "AND manufacturers.is_deleted = FALSE "
                + "AND cars.id IN (SELECT car_id FROM cars_drivers WHERE driver_id = ?)";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(getAllCarByDriverQuery)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                List<Driver> drivers = getDriversByCarId(connection,
                        resultSet.getObject(1, Long.class));
                cars.add(getCar(resultSet, drivers));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driverId "
                    + driverId + " from carsDB.", e);
        }
    }

    private void removeDriversFromCar(Connection connection, Long carId) {
        String createQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (PreparedStatement createStatement
                     = connection.prepareStatement(createQuery)) {
            createStatement.setLong(1, carId);
            createStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers from car by id "
                    + carId + " in DB.", e);
        }
    }

    private void addDriversToCar(Connection connection, Car car) {
        String createQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?); ";
        try (PreparedStatement createStatement
                     = connection.prepareStatement(createQuery)) {
            for (Driver driver : car.getDrivers()) {
                createStatement.setLong(1, car.getId());
                createStatement.setLong(2, driver.getId());
                createStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add drivers to car "
                    + car + " in DB.", e);
        }
    }

    private List<Driver> getDriversByCarId(Connection connection,
                                           Long carId) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String getQuery = "SELECT drivers.id, drivers.name, drivers.license_number "
                + "FROM cars_drivers "
                + "JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ? "
                + "AND drivers.is_deleted = FALSE";
        try (PreparedStatement getStatement
                     = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, carId);
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers to car by id: " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString(2));
        driver.setLicenseNumber(resultSet.getString(3));
        return driver;
    }

    private Car getCar(ResultSet resultSet,
                       List<Driver> drivers) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));
        car.setManufacturer(getManufacturer(resultSet));
        car.setDrivers(drivers);
        return car;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(3, Long.class));
        manufacturer.setName(resultSet.getString(4));
        manufacturer.setCountry(resultSet.getString(5));
        return manufacturer;
    }
}
