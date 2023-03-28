package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
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
                PreparedStatement statement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1,car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generateKeys = statement.getGeneratedKeys();
            if (generateKeys.next()) {
                Long id = generateKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id, model, manufacturer_id, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE AND c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE;";

        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m "
                + " ON c.manufacturer_id = m.id"
                + " JOIN cars_drivers cd"
                + " ON cd.car_id = c.id"
                + " WHERE cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can get all cars by driverId: " + driverId, e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "Update cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? and is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car:" + car, e);
        }
        deleteAllDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "Update cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id, e);
        }
    }

    public Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id",Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(resultSet
                .getObject("manufacturer_id",Long.class),
                resultSet.getString("name"), resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    public List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT id, d.name, license_number"
                + " FROM drivers d"
                + " JOIN cars_drivers cd"
                + " ON d.id = driver_id"
                + " WHERE cd.carId = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(query,Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1,carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getObject("id",Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("license_number"));
                driverList.add(driver);
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by cars id: " + carId, e);
        }
    }

    private void deleteAllDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete all drivers from car: " + car, e);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2,driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into car: " + car, e);
        }
    }
}
