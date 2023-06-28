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
        String query = "INSERT INTO `cars` (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                insertDrivers(car);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query =
                "SELECT `c`.`id` AS `car_id`, `c`.`model`, `m`.`id` AS `manufacturer_id`,"
                + " `m`.`name`, `m`.`country` "
                + "FROM `cars` `c` "
                + "LEFT JOIN `manufacturers` `m`"
                + "ON `c`.`manufacturer_id` = `m`.`id` "
                + "WHERE `c`.`is_deleted` = FALSE and `m`.`is_deleted` = FALSE and `c`.`id` = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT `c`.`id` AS `car_id`, `c`.`model`, `m`.`id` AS `manufacturer_id`, "
                + "`m`.`name`, `m`.`country` "
                + "FROM `cars` `c`"
                + "LEFT JOIN `manufacturers` `m` "
                + "ON `c`.`manufacturer_id` = `m`.`id` "
                + "WHERE `c`.`is_deleted` = FALSE and `m`.`is_deleted` = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT `c`.`id` AS `car_id`, `c`.`model`, `m`.`id` AS `manufacturer_id`, "
                + "`m`.`name`, `m`.`country` "
                + "FROM `cars_drivers` `cd`"
                + "INNER JOIN `cars` `c`"
                + "ON `c`.`id` = `cd`.`car_id` "
                + "LEFT JOIN `manufacturers` `m`"
                + "ON `m`.`id` = `c`.`manufacturer_id` "
                + "WHERE `cd`.`driver_id` = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driver Id = "
                    + driverId + " from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE `cars` "
                + "SET `manufacturer_id` = ?, `model` = ? "
                + "WHERE `id` = ? AND `is_deleted` = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
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

    private List<Driver> getDriversFromCar(Long id) {
        String getAllDriversQuery =
                "SELECT `d`.`id`, `d`.`name`, `d`.`license_number` "
                + "FROM `drivers` `d`"
                + "JOIN `cars_drivers` `cd` "
                + "ON `d`.`id` = `cd`.`driver_id` "
                + "WHERE `cd`.`car_id` = ?;";
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

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        return new Car(id, model, getManufacturer(resultSet), getDriversFromCar(id));
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        return new Manufacturer(id, name, country);
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO `cars_drivers` (car_id, driver_id) VALUES (?, ?);";
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

    private void deleteDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM `cars_drivers` WHERE `car_id` = ?;";
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
