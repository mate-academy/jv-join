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
        String insertCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(insertCarQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, car.getModel());
            insertStatement.setLong(2, car.getManufacturer().getId());
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, m.id AS manufacturer_id, "
                + "c.model, m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection getCarConnection = ConnectionUtil.getConnection();
                PreparedStatement statement = getCarConnection.prepareStatement(getCarQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversFromCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarQuery = "SELECT c.id AS car_id, model, "
                + "manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement
                        = connection.prepareStatement(getAllCarQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table.", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversFromCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setLong(1, car.getManufacturer().getId());
            updateStatement.setString(2, car.getModel());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't update " + car + " in cars DB.", e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name, m.country FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(
                        getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't get a list of cars by driver id: " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversFromCar(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection insertDriverConnection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement
                        = insertDriverConnection.prepareStatement(insertDriversQuery)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert driver to car " + car, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        return new Car(id, model, manufacturer);
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        return new Manufacturer(id, name, country);
    }

    private List<Driver> getDriversFromCar(Long id) {
        String getAllDriversQuery = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDriversQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get Drivers from DB by Car id " + id, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void deleteDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(deleteDriversQuery)) {
            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers by car: " + car, e);
        }
    }
}
