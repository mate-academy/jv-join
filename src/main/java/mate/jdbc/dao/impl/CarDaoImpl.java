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
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT `cars`.`id` AS `car_id`, `cars`.`model` AS `car_model`, "
                + "`manufacturers`.`id` AS `manufacturer_id`, `manufacturers`.`name` AS `manufacturer_name`, "
                + "`manufacturers`.`country` AS `manufacturer_country` "
                + "FROM `cars` "
                + "LEFT JOIN `manufacturers` "
                + "ON `cars`.`manufacturer_id` = `manufacturers`.`id` "
                + "WHERE `cars`.`is_deleted` = FALSE and `manufacturers`.`is_deleted` = FALSE and `cars`.`id` = ?;";
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
        String query = "SELECT `cars`.`id` AS `car_id`, `cars`.`model` AS `car_model`, "
                + "`manufacturers`.`id` AS `manufacturer_id`, `manufacturers`.`name` AS `manufacturer_name`, "
                + "`manufacturers`.`country` AS `manufacturer_country` "
                + "FROM `cars` "
                + "LEFT JOIN `manufacturers` "
                + "ON `cars`.`manufacturer_id` = `manufacturers`.`id` "
                + "WHERE `cars`.`is_deleted` = FALSE and `manufacturers`.`is_deleted` = FALSE;";
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
        String query = "SELECT `cars`.`id` AS `car_id`, `cars`.`model` AS `car_model`, "
                 + "`manufacturers`.`id` AS `manufacturer_id`, `manufacturers`.`name` AS `manufacturer_name`, "
                 + "`manufacturers`.`country` AS `manufacturer_country` "
                 + "FROM `cars_drivers` "
                 + "INNER JOIN `cars` "
                 + "ON `cars`.`id` = `cars_drivers`.`car_id` "
                 + "LEFT JOIN `manufacturers` "
                 + "ON `manufacturers`.`id` = `cars`.`manufacturer_id` "
                 + "WHERE `cars_drivers`.`driver_id` = ? ";
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
        return null;
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

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("car_model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("manufacturer_name"),
                resultSet.getString("manufacturer_country"));
        return new Car(id, model, manufacturer, getDriversIdByCarId(id));
    }

    private List<Driver> getDriversIdByCarId (Long id) {
        String query = "SELECT driver_id "
                + "FROM cars_drivers "
                + "WHERE car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(new Driver(resultSet.getObject("driver_id", Long.class)));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of driver id`s from car_driversDB.", e);
        }
    }

}
