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
        String sqlQuery = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection
                        .prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't create car in db " + car, exception);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String sqlQuery = "SELECT c.model car_model, m.id manufacturer_id, "
                + "m.name manufacturer_name, m.country manufacturer_country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(sqlQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = processCar(resultSet);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't get car from db with id " + id, exception);
        }
        if (car != null) {
            List<Driver> drivers = getDrivers(id);
            car.setDrivers(drivers);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String sqlQuery = "SELECT c.id car_id, c.model car_model, m.id manufacturer_id, "
                + "m.name manufacturer_name, m.country manufacturer_country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(processCar(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't get all cars from db", exception);
        }
        for (Car c : cars) {
            c.setDrivers(getDrivers(c.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String sqlQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(sqlQuery)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.setLong(3, car.getId());
            createStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't update car in db " + car, exception);
        }
        deleteFromCarsDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id " + id, e);
        }
    }

    private void insertDrivers(Car car) {
        String sqlQuery = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(sqlQuery)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into db from car: " + car, e);
        }
    }

    private List<Driver> getDrivers(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String sqlQuery = "SELECT id, name, license_number "
                + "FROM drivers JOIN cars_drivers ON car_id = ? WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDrivers = connection.prepareStatement(sqlQuery)) {
            getDrivers.setLong(1, carId);
            ResultSet resultSet = getDrivers.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                String name = resultSet.getString(2);
                String licenseNumber = resultSet.getString(3);
                Driver driver = new Driver(id, name, licenseNumber);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id " + carId, e);
        }
        return drivers;
    }

    private Car processCar(ResultSet resultSet) {
        Car car = new Car();
        try {
            Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
            String manufacturerName = resultSet.getString("manufacturer_name");
            String manufacturerCountry = resultSet.getString("manufacturer_country");
            Manufacturer manufacturer =
                    new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
            car.setId(resultSet.getObject("car_id", Long.class));
            car.setManufacturer(manufacturer);
            car.setModel(resultSet.getString("car_model"));
        } catch (SQLException e) {
            throw new DataProcessingException("Can't process car from ResultSet", e);
        }
        return car;
    }

    private void deleteFromCarsDrivers(Long carId) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setLong(1, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations "
                    + "in cars_drivers table for car with id " + carId, e);
        }
    }
}
