package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String request = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                         .prepareStatement(request, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create car: " + car, throwable);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String request = "SELECT cars.id AS id, cars.model AS model, "
                + "manufacturers.id AS manufacturer_id,\n"
                + "manufacturers.name AS name, manufacturers.country AS country FROM cars \n"
                + "JOIN manufacturers ON manufacturers.id = cars.manufacturer_id\n"
                + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(request)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = createCarObject(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String request = "SELECT * "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "   ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet = getAllStatement.executeQuery(request);
            while (resultSet.next()) {
                cars.add(createCarObject(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String request = "SELECT cars.id, model, cars.is_deleted, manufacturer_id, name, country "
                + "FROM cars "
                + "JOIN cars_drivers cd on cars.id = cd.car_id "
                + "JOIN manufacturers m ON cars.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND m.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(request)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCarObject(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id: " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String request = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(request)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can' update car: " + car, e);
        }
        deleteOldRelations(car.getId());
        List<Driver> drivers = car.getDrivers();
        if (drivers != null) {
            for (Driver driver : drivers) {
                createNewRelations(car);
            }
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String request = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(request)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id, e);
        }
    }

    private Car createCarObject(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long carId = resultSet.getObject("id", Long.class);
        String carModel = resultSet.getString("model");
        car.setId(carId);
        car.setModel(carModel);

        Manufacturer manufacturer = new Manufacturer();
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        manufacturer.setId(manufacturerId);
        manufacturer.setName(manufacturerName);
        manufacturer.setCountry(manufacturerCountry);
        return car;
    }

    private List<Driver> getDriversByCarId(Long id) {
        List<Driver> driversList = new ArrayList<>();
        String request = "SELECT * FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(request)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driversList.add(createDriverObject(resultSet));
            }
            return driversList;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get driver by car id: " + id, e);
        }
    }

    private Driver createDriverObject(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        Long driverId = resultSet.getObject("id", Long.class);
        String driverName = resultSet.getString("name");
        String driverLicenseNumber = resultSet.getString("license_number");
        driver.setId(driverId);
        driver.setName(driverName);
        driver.setLicenseNumber(driverLicenseNumber);
        return driver;
    }

    private void deleteOldRelations(Long id) {
        String request = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement = connection.prepareStatement(request)) {
            deleteRelationsStatement.setLong(1, id);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations "
                    + "between car and driver by id: " + id, e);
        }
    }

    private void createNewRelations(Car car) {
        String request = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationsStatement = connection.prepareStatement(request)) {
            createRelationsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createRelationsStatement.setLong(2, driver.getId());
                createRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create relations between "
                    + "driver and car: " + car, e);
        }
    }
}
