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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDriverToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement
                         = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by ID " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarQuery = "SELECT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> allCarsList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarStatement
                         = connection.prepareStatement(getAllCarQuery)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                allCarsList.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car list from DB", e);
        }
        for (Car car : allCarsList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return allCarsList;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement =
                         connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        removeDriverFromCar(car.getId());
        insertDriverToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement
                         = connection.prepareStatement(deleteQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
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
            statement.setLong(1, driverId);
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

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT d.name AS driver_name, "
                + "d.license_number AS driver_licence_number, "
                + "d.id AS driver_id "
                + "FROM drivers d "
                + "INNER JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        List<Driver> allDriverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement
                         = connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                allDriverList.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't create a list of driver for car with ID " + carId, e);
        }
        return allDriverList;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("driver_licence_number"));
        return driver;
    }

    private void insertDriverToCar(Car car) {
        String insertDriverToCarQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriverToCarStatement
                         = connection.prepareStatement(insertDriverToCarQuery)) {
            for (Driver driver : car.getDrivers()) {
                insertDriverToCarStatement.setLong(1, car.getId());
                insertDriverToCarStatement.setLong(2, driver.getId());
                insertDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car: " + car, e);
        }
    }

    private void removeDriverFromCar(Long carId) {
        String removeDriverFromCarQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                removeDriverFromCarStatement
                = connection.prepareStatement(removeDriverFromCarQuery)) {
            removeDriverFromCarStatement.setLong(1, carId);
            removeDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver: " + carId, e);
        }
    }
}
