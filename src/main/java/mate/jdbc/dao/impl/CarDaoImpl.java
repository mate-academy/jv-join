package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUE (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query =
                "SELECT c.id AS car_id, c.model,m.id AS manufacturer_id, m.name, m.country " +
                        "FROM cars c " +
                        "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                        "WHERE c.id = ? " +
                        "AND c.is_deleted = FALSE; ";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (car != null) {
            List<Driver> drivers = getDriversForCar(id);
            car.setDrivers(drivers);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query =
                "SELECT c.id AS car_id, c.model,m.id AS manufacturer_id, m.name, m.country " +
                        "FROM cars c " +
                        "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                        "WHERE c.is_deleted = FALSE; ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                List<Driver> drivers = getDriversForCar(car.getId());
                car.setDrivers(drivers);
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                               + "SET model= ?, manufacturer_id=?"
                               + "WHERE  id =? AND is_deleted=FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        deleteAllColumnsByCarId(car);
        insertDrivers(car);
        return car;
    }

    private void deleteAllColumnsByCarId(Car car) {
        String query = "DELETE FROM drivers_cars " +
                               "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars "
                               + "SET is_deleted=TRUE " +
                               "WHERE  id =? AND is_deleted=FALSE";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeUpdate() > 0;

        } catch (
                  SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String selectDriversQuery = "SELECT d.id, d.name, d.licence_number " +
                                            "FROM drivers d " +
                                            "JOIN drivers_cars dc ON d.id = dc.driver_id " +
                                            "WHERE dc.car_id = ? " +
                                            "AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectDriversQuery)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
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
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString(2));
        driver.setLicenseNumber(resultSet.getString(3));
        return driver;
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = parseManufacturer(resultSet);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO drivers_cars(driver_id, car_id)" +
                               " VALUES (?,?) ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
