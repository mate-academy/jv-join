package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(
                     createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car to DB", e);
        }
        insertDrivers(car);
        return car;
    }



    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, model, name, country, m.id AS manufacturer_id, name, country\n" +
                "FROM cars c\n" +
                "JOIN manufacturers m\n" +
                "ON c.manufacturer_id = m.id\n" +
                "where c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }


    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id, model, name, country, m.id AS manufacturer_id, name, country\n" +
                "FROM taxi_service.cars c\n" +
                "JOIN taxi_service.manufacturers m\n" +
                "ON c.manufacturer_id = m.id\n" +
                "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars");
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars\n" +
                "SET model = ?, manufacturer_id = ?\n" +
                "where id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement updateCarStatement = connection.prepareStatement(updateQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car", e);
        }
            deleteRelations(car.getId());
            insertDrivers(car);
        return car;
    }

    private void deleteRelations(Long carID) {
        String deletedQuery = "DELETE cd\n" +
                "FROM cars_drivers cd\n" +
                "JOIN cars c\n" +
                "ON cd.car_id = c. id\n" +
                "WHERE c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement deletedStatement = connection.prepareStatement(deletedQuery)) {
            deletedStatement.setLong(1, carID);
            deletedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations from DB", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String updateQuery = "UPDATE";
        return false;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriverToCarStatement = connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver to car");
        }
    }

    private List<Driver> getDriversForCar(Long carID) {
        String getAllDriversQuery = "SELECT id, name, license_number\n" +
                "FROM drivers d \n" +
                "JOIN cars_drivers cd\n" +
                "ON d.id = cd.driver_id\n" +
                "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement getAllDriversStatement = connection.prepareStatement(getAllDriversQuery)){
            getAllDriversStatement.setLong(1, carID);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromCar(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers", e);
        }
    }

    private Driver parseDriversFromCar(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
