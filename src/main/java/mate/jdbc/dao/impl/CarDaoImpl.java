package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertRequest,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet generateKeys = statement.getGeneratedKeys();
            if (generateKeys.next()) {
                Long id = generateKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS car_id, c.model AS car_model, "
                + "c.manufacturer_id, m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement((getQuery))) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id:" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model AS car_model, c.manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars from cars table", e);
        }
        cars.forEach(car -> car.setDrivers(getDrivers(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String queryUpdateCar = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryUpdateCar)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.setObject(3, car.getId());
            statement.executeUpdate();
            removeDrivers(car);
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with id = " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(deleteCarQuery)) {
            statement.setLong(1, carId);
            int numbersOfDeletedRows = statement.executeUpdate();
            return numbersOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model AS car_model, "
                + "m.id AS manufacturer_id, m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON d.id = cd.driver_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE d.id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                Manufacturer manufacturer = getManufacturer(resultSet);
                car.setManufacturer(manufacturer);
                car.setDrivers(getDrivers(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id: " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("car_model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        return new Car(id, model, manufacturer);
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        return new Manufacturer(id, name, country);
    }

    private static void removeDrivers(Car car) throws SQLException {
        String queryDeleteCarsDrivers = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(queryDeleteCarsDrivers)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        }
    }

    private void insertDrivers(Car car) {
        String insertAuthorsQuery = "INSERT INTO cars_drivers (car_id, driver_id)"
                + "VAlUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertAuthorsQuery)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver to car: " + car, e);
        }
    }

    private List<Driver> getDrivers(Long carId) {
        String getAllDriversForCarRequest = "SELECT id AS driver_id, d.name AS driver_name, "
                + "d.license_number AS driver_license_number "
                + "FROM drivers AS d "
                + "JOIN cars_drivers AS cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find driver for car:" + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("driver_name");
        String licenseNumber = resultSet.getString("driver_license_number");
        return new Driver(id, name, licenseNumber);
    }
}
