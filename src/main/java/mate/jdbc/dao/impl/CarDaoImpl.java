package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
                PreparedStatement preparedStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, car.getManufacturer().getId());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            addDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add to DB: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id,"
                + " manufacturers.name AS manufacturer_name,"
                + " manufacturers.country AS manufacturer_country FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Car car = createCar(resultSet);
                Manufacturer manufacturer = createmManufacturer(resultSet);
                List<Driver> drivers = getDriversFromDB(id);
                car.setManufacturer(manufacturer);
                car.setDrivers(drivers);
                return Optional.of(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get Car by id:" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT id FROM cars WHERE is_deleted = FALSE;";
        List<Long> indexes = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                indexes.add(resultSet.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars", e);
        }
        return indexes.stream()
                .map(i -> get(i).orElseThrow(
                        () -> new RuntimeException("Can't get car by id: " + i)))
                .collect(Collectors.toList());
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car " + car, e);
        }
        deleteRelationship(car);
        addDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT id FROM cars JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ? AND cars.is_deleted = FALSE;";
        List<Long> indexes = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                indexes.add(resultSet.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars by driverId + " + driverId, e);
        }
        return indexes.stream()
                .map(i -> get(i).orElseThrow(
                    () -> new RuntimeException("Can't get car by id: " + i)))
                .collect(Collectors.toList());
    }

    private Manufacturer createmManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        return new Manufacturer(id, name, country);
    }

    private Car createCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        return car;
    }

    private List<Driver> getDriversFromDB(Long id) {
        List<Driver> result = new ArrayList<>();
        String query = "SELECT id, name, licenseNumber FROM drivers"
                + " JOIN cars_drivers AS cd ON drivers.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(creatDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers by id " + id, e);
        }
        return result;
    }

    private Driver creatDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("licenseNumber");
        return new Driver(id, name, licenseNumber);
    }

    private void deleteRelationship(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car: " + car, e);
        }
    }

    private void addDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(1, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't add driver for car: " + car, e);
        }
    }
}
