package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet resultSet = insertCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car: " + car, e);
        }
        addDriverToCar(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id AS car_id, c.model AS car_model, "
                + "m.id AS manufacturer_id, m.name AS manufacturer_name,"
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        if (car != null) {
            List<Driver> drivers = getDriversForCar(car.getId());
            car.setDrivers(drivers);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model AS car_model, m.name AS manufacturer_name, "
                + "m.id AS manufacturer_id,"
                + "country AS manufacturer_country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection
                        .prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithManufacturer(resultSet);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection
                        .prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        removeDriverFromCar(car);
        if (car.getDrivers() != null) {
            addDriverToCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection
                        .prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.model AS car_model, c.id AS car_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country, "
                + "m.id AS manufacturer_id  FROM cars_drivers cd "
                + "LEFT JOIN cars c ON cd.car_id = c.id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByCarStatement = connection
                        .prepareStatement(query)) {
            getAllByCarStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all by driver's id: " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id as driver_id, d.name as driver_name, "
                + "d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND d.deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDrivers = connection
                        .prepareStatement(query)) {
            getDrivers.setLong(1, id);
            ResultSet resultSet = getDrivers.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car by id: " + id, e);
        }
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet.getString("manufacturer_name"),
                resultSet.getString("manufacturer_country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        Car car = new Car();
        car.setModel(resultSet.getString("car_model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("driver_name"), resultSet.getString(
                "license_number"));
        driver.setId((resultSet.getObject("driver_id", Long.class)));
        return driver;
    }

    private void addDriverToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverStatement =
                        connection.prepareStatement(query)) {
            addDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverStatement.setLong(2, driver.getId());
                addDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to car: " + car, e);
        }
    }

    private void removeDriverFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverStatement =
                        connection.prepareStatement(query)) {
            removeDriverStatement.setLong(1, car.getId());
            removeDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove driver from car: " + car, e);
        }
    }
}
