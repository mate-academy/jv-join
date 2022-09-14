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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return insertDrivers(car, connection);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, m.id AS manufacturer_id, model, m.name, m.country "
                     + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                     + "WHERE c.is_deleted = FALSE AND c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet, connection);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, m.id AS manufacturer_id, model, m.name, m.country "
                     + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                     + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet, connection));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                     + "WHERE is_deleted = FALSE AND id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            deleteDriversFromCar(car.getId(), connection);
            return insertDrivers(car, connection);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, manufacturer_id, name, country FROM cars c "
                     + "JOIN cars_drivers cd ON c.id = cd.car_id "
                     + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                     + "WHERE c.is_deleted = FALSE ANS cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet, connection));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB "
                    + "by driver id " + driverId, e);
        }
    }

    private Car insertDrivers(Car car, Connection connection) throws SQLException {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, car.getId());
        for (Driver driver : car.getDrivers()) {
            statement.setLong(2, driver.getId());
            statement.executeUpdate();
        }
        return car;
    }

    private Car getCar(ResultSet resultSet, Connection connection) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("model");
        List<Driver> drivers = getDriversByCar(carId, connection);
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        return new Car(
                carId,
                carModel,
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry),
                drivers
        );
    }

    private List<Driver> getDriversByCar(Long carId, Connection connection) throws SQLException {
        String query = "SELECT id, name, license_number FROM drivers d "
                     + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                     + "WHERE d.is_deleted = FALSE AND cd.car_id = ?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, carId);
        ResultSet resultSet = statement.executeQuery();
        List<Driver> drivers = new ArrayList<>();
        while (resultSet.next()) {
            drivers.add(getDriver(resultSet));
        }
        return drivers;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void deleteDriversFromCar(Long carId, Connection connection) throws SQLException {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, carId);
        statement.executeUpdate();
    }
}
