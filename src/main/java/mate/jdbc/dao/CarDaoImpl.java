package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement createCarStatement
                = connection.prepareStatement(createCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create a car " + car, e);
        }
        insertCarDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT c.id AS car_id, c.model AS car_model, "
                + "m.name AS manufacturer_name, m.id AS manufacturer_id, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE m.is_deleted = FALSE AND c.is_deleted = FALSE "
                + "AND c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getCarStatement
                = connection.prepareStatement(getCarRequest)) {
            getCarStatement.setObject(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                Manufacturer manufacturerEntity = getManufacturer(resultSet);
                Car car = getCar(resultSet, manufacturerEntity);
                car.setDrivers(getDriversForCar(car.getId()));
                return Optional.of(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car by ID " + id + " from DB", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT c.id AS car_id, c.model AS car_model, "
                + "m.name AS manufacturer_name, m.id AS manufacturer_id, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE m.is_deleted = FALSE AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getAllStatement
                = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                Manufacturer manufacturerEntity = getManufacturer(resultSet);
                Car carEntity = getCar(resultSet, manufacturerEntity);
                cars.add(carEntity);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        for (Car car : cars) {
            List<Driver> driversForCar = getDriversForCar(car.getId());
            car.setDrivers(driversForCar);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement =
                     connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car, e);
        }
        removeCarDrivers(car);
        insertCarDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?"
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement =
                     connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setObject(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car with ID " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsQuery = "SELECT driver_id, c.model AS car_model, car_id, model, "
                + "m.country AS manufacturer_country, m.name AS manufacturer_name, "
                + "manufacturer_id FROM cars_drivers cd "
                + "INNER JOIN cars c "
                + "ON c.id = cd.car_id "
                + "INNER JOIN manufacturers AS m "
                + "ON m.id = c.manufacturer_id "
                + "WHERE driver_id = ? AND m.is_deleted = FALSE AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsStatement =
                     connection.prepareStatement(getCarsQuery)) {
            getCarsStatement.setObject(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                Manufacturer manufacturerEntity = getManufacturer(resultSet);
                Car carEntity = getCar(resultSet, manufacturerEntity);
                cars.add(carEntity);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver ID " + driverId, e);
        }
        for (Car car : cars) {
            List<Driver> driversForCar = getDriversForCar(car.getId());
            car.setDrivers(driversForCar);
        }
        return cars;
    }

    private void insertCarDrivers(Car car) {
        String putDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement putDriversStatement
                = connection.prepareStatement(putDriversRequest)) {
            putDriversStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                putDriversStatement.setObject(2, driver.getId());
                putDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers for a car " + car, e);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        return manufacturer;
    }

    private Car getCar(ResultSet resultSet, Manufacturer manufacturer)
            throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversRequest = "SELECT * FROM cars_drivers cd "
                + "JOIN drivers d ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversStatement =
                     connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setObject(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by car ID "
                    + carId + " from DB", e);
        }
        return drivers;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private void removeCarDrivers(Car car) {
        String removeDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteDriversStatement =
                     connection.prepareStatement(removeDriversRequest)) {
            deleteDriversStatement.setObject(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers of car " + car, e);
        }
    }
}
