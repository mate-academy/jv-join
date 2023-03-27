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
    private static final int CAR_ID_INDEX = 1;
    private static final int DRIVER_ID_INDEX = 2;
    private static final int FIRST_INDEX = 1;
    private static final int SECOND_INDEX = 2;
    private static final int THIRD_INDEX = 3;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(FIRST_INDEX, car.getModel());
            statement.setLong(SECOND_INDEX, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(FIRST_INDEX, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to add " + car + " to DB", e);
        }
        insertCarDrivers(car);
        return car;
    }

    private void insertCarDrivers(Car car) {
        String query = "INSERT INTO cars_drivers VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(CAR_ID_INDEX, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(DRIVER_ID_INDEX, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to insert Drivers for " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT c.id, model, m.id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarAndManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to get car from DB. Id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    private Car parseCarAndManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getLong("m.id");
        String manufacturerName = resultSet.getString("m.name");
        String manufacturerCountry = resultSet.getString("m.country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerName, manufacturerCountry);
        Long carId = resultSet.getLong("c.id");
        String carModel = resultSet.getString("model");
        return new Car(carId, carModel, manufacturer, null);
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM cars_drivers cd "
                + "INNER JOIN drivers d "
                + "ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to get Drivers for car. Id: " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getLong("d.id");
        String driverName = resultSet.getString("d.name");
        String licenseNumber = resultSet.getString("d.license_number");
        return new Driver(driverId, driverName, licenseNumber);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, model, m.id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarAndManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, car.getManufacturer().getId());
            statement.setString(SECOND_INDEX, car.getModel());
            statement.setLong(THIRD_INDEX, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to update " + car, e);
        }
        deleteAllCarDrivers(car);
        insertCarDrivers(car);
        return car;
    }

    private void deleteAllCarDrivers(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to delete all drivers for car :" + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars "
                + "SET is_deleted = TRUE "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to delete car. Id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Long> carIDs = new ArrayList<>();
        String query = "SELECT car_id "
                + "FROM cars_drivers "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carIDs.add(resultSet.getLong("car_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to get all "
                    + "car IDs with driver that has ID:" + driverId, e);
        }
        List<Car> cars = new ArrayList<>();
        for (Long id : carIDs) {
            get(id).ifPresent(cars::add);
        }
        return cars;
    }
}
