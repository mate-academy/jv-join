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
        String createRequest = "INSERT INTO cars(manufacturer_id, model) VALUES(?, ?);";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = dbConnection.prepareStatement(createRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car: " + car
                    + " to DB", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id,"
                + " m.name, m.country FROM cars c INNER JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE"
                + " AND c.id = ?;";
        Car car = null;
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = dbConnection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id
                    + " from DB", e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id,"
                + " m.name, m.country FROM cars c INNER JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id WHERE c.is_deleted = FALSE "
                + "AND m.is_deleted = FALSE;";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = dbConnection.prepareStatement(getRequest)) {
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ? "
                + "AND is_deleted = FALSE;";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = dbConnection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update DB by car:" + car, e);
        }
        updateCarsDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = dbConnection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id
                    + " from DB", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, "
                + "m.name, m.country FROM cars c INNER JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id RIGHT JOIN cars_drivers cd "
                + "ON cd.car_id = c.id WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE "
                + "AND cd.driver_id = ?;";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = dbConnection.prepareStatement(getRequest)) {
            getStatement.setLong(1, driverId);
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id: " + driverId
                    + " from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = dbConnection.prepareStatement(insertRequest)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers from car " + car
                    + " to DB", e);
        }
    }

    private List<Driver> getDriversByCarId(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String getRequest = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "INNER JOIN cars_drivers ON cars_drivers.driver_id = d.id "
                + "WHERE cars_drivers.car_id = ?;";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = dbConnection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(new Driver(resultSet.getObject("id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("license_number")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by car id: " + id, e);
        }
        return drivers;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        return new Car(resultSet.getObject("car_id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(resultSet
                        .getObject("manufacturer_id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")), null);
    }

    private void updateCarsDrivers(Car car) {
        String deleteOldDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        String insertNewDriversRequest = "INSERT INTO cars_drivers(car_id, driver_id) "
                + "VALUES(?, ?);";
        try (Connection dbConnection = ConnectionUtil.getConnection();
                PreparedStatement deleteOldDriversStatement = dbConnection
                        .prepareStatement(deleteOldDriversRequest);
                PreparedStatement insertNewDriversStatement = dbConnection
                        .prepareStatement(insertNewDriversRequest)) {
            deleteOldDriversStatement.setLong(1, car.getId());
            deleteOldDriversStatement.executeUpdate();
            insertNewDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertNewDriversStatement.setLong(2, driver.getId());
                insertNewDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update DB by car:" + car, e);
        }
    }
}
