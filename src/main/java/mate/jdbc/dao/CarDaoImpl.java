package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert to DB car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(driver_id, car_id) VALUES(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driversToDb from Car: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS id, model, manufacturers.id AS manufacturer_id ,"
                + "manufacturers.name AS name, manufacturers.country AS country"
                + " FROM cars"
                + " JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromDb(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversFromDb(id));
        }
        return Optional.ofNullable(car);
    }

    private Car parseCarFromDb(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversFromDb(Long id) {
        String query = "SELECT drivers.id AS id, drivers.name AS name,"
                + " drivers.license_number AS license_number "
                + "FROM drivers "
                + "JOIN cars_drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromDb(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers from DB by id: " + id, e);
        }
        return drivers;
    }

    private Driver parseDriverFromDb(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getLong("id"));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id AS id, cars.model AS model, "
                + "manufacturers.id AS manufacturer_id, manufacturers.name"
                + " AS name , manufacturers.country AS country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromDb(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all data from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversFromDb(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in db. Car: " + car, e);
        }
        deleteAllRelations(car);
        insertDrivers(car);
        return car;
    }

    private void deleteAllRelations(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations with Car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = false WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "JOIN manufacturers "
                + "ON manufacturers.id = cars.manufacturer_id "
                + "WHERE cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setModel(resultSet.getString("model"));
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
                manufacturer.setId(resultSet.getObject("manufacturer_id"
                        , Long.class));
                car.setManufacturer(manufacturer);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from"
                    + " DB with driver id: " + driverId, e);
        }
        for (Car car : cars) {
            List<Driver> drivers = getDriversFromDb(car.getId());
            car.setDrivers(drivers);
        }
        return cars;
    }
}
