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
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, color, manufacturer_id) "
                + "VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setString(2, car.getColor());
            statement.setLong(3, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                insertDrivers(car);
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id as car_id, model, color, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get driver by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(findAllDriversByCarID(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id as car_id, model, color, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet = getAllStatement.executeQuery(getAllQuery);
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Error happened when getting "
                    + "all cars from db", exception);
        }
        cars.forEach(car -> car.setDrivers(findAllDriversByCarID(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, color = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        int updatedAmount;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement =
                        connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setString(2, car.getColor());
            updateStatement.setLong(3, car.getManufacturer().getId());
            updateStatement.setLong(4, car.getId());
            updatedAmount = updateStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in driversDB.", throwable);
        }
        if (updatedAmount > 0) {
            deleteDrivers(car.getId());
            insertDrivers(car);
            return car;
        }
        throw new DataProcessingException("Car with id - " + car.getId()
                + " wasn't found in DB");
    }

    private void deleteDrivers(Long carId) {
        String query = "DELETE FROM cars_drivers where car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete drivers from car with id "
                    + carId, throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> findAllByDriverID(Long driverId) {
        String findQuery = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement findStatement = connection.prepareStatement(findQuery)) {
            findStatement.setLong(1, driverId);
            ResultSet resultSet = findStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't find cars by driver id "
                    + driverId + ". ", throwable);
        }
        cars.forEach(car -> car.setDrivers(findAllDriversByCarID(car.getId())));
        return cars;
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        String color = resultSet.getString("color");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, color, manufacturer);
        car.setId(carId);
        return car;
    }

    private List<Driver> findAllDriversByCarID(Long carId) {
        String findRequest = "SELECT * FROM drivers join cars_drivers cd "
                + "ON drivers.id = cd.driver_id where car_id = ? "
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement findStatement = connection.prepareStatement(findRequest)) {
            findStatement.setLong(1, carId);
            ResultSet resultSet = findStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't find all drivers of the car by car id "
                    + carId + ". ", throwable);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement
                        = connection.prepareStatement(insertRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert drivers to car with id "
                    + car.getId(), throwable);
        }
    }
}
