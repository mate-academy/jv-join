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

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB: " + car, e);
        }
        setDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query
                = "SELECT c.id as car_id, model, c.is_deleted, manufacturer_id, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE AND c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query =
                "SELECT c.id as car_id, c.model as model, m.id as idm,"
                        + " m.name as name, m.country as country FROM cars c"
                        + " JOIN manufacturers m"
                        + " ON c.manufacturer_id=m.id"
                        + " WHERE c.is_deleted = FALSE;";
        return getHandler(query, -1L);
    }

    @Override
    public Car update(Car car) {
        String query =
                "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        updateDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query =
                "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT id"
                + " FROM cars c"
                + " JOIN cars_drivers cd"
                + " ON c.id = cd.driver_id"
                + " WHERE cd.car_id = ? AND c.is_deleted = FALSE;";
        return getHandler(query, driverId);
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        return new Car(resultSet.getObject("car_id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(
                        resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")));
    }

    private List<Car> getHandler(String query, Long driverId) {
        List<Car> carsModels = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            if (driverId > 0) {
                statement.setLong(1, driverId);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsModels.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    driverId > 0 ? "Can't get cars from DB by driver id: "
                            : "Can't get all cars from DB"
                    + driverId, e);
        }
        carsModels.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return carsModels;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String driversQuery = "SELECT id, name, license_number"
                + " FROM drivers d"
                + " JOIN cars_drivers cd"
                + " ON d.id = cd.driver_id"
                + " WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(driversQuery)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id: " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void updateDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete drivers from DB by car id: " + car.getId(), e
            );
        }
        setDrivers(car);
    }

    private void setDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver: car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers " + car.getDrivers(), e);
        }
    }
}
