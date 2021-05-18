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
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement = connection.prepareStatement(createQuery,
                         Statement.RETURN_GENERATED_KEYS)) {

            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car, e);
        }
        insetrDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name as  manufacturer_name, country as manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection.prepareStatement(getQuery)) {

            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        if (car != null) {
            car.setDriver(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name as  manufacturer_name, country as manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement = connection
                         .prepareStatement(getAllQuery)) {

            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all ", e);
        }
        if (cars != null) {
            for (Car car: cars) {
                car.setDriver(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update " + car, e);
        }
        deleteRelationsInCars_drivers(car.getId());
        insetrDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteDriverStatement = connection
                         .prepareStatement(deleteQuery)) {
            deleteDriverStatement.setLong(1, id);
            return deleteDriverStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllCarByDriver(Long driverId) {
        String getAllQuery = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name as  manufacturer_name, country as manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllQuery)) {

            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car by driver " + driverId, e);
        }
        for (Car car: cars) {
            car.setDriver(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void insetrDrivers(Car car) {
        String insetrDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id ) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriversToCarStatement = connection
                         .prepareStatement(insetrDriversQuery)) {

            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriver()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert car: " + car, throwables);
        }
    }

    private boolean deleteRelationsInCars_drivers(Long id) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement = connection.prepareStatement(deleteQuery)) {
            updateStatement.setLong(1, id);
            return updateStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete relations, id: " + id, throwables);
        }
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));

        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversFromCarQuery = "SELECT id, name, license_number "
                + "FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement = connection
                         .prepareStatement(getAllDriversFromCarQuery)) {

            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> driver = new ArrayList<>();
            while (resultSet.next()) {
                driver.add(parseDrivers(resultSet));
            }
            return driver;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get drivers for car " + carId, throwables);
        }
    }

    private Driver parseDrivers(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
