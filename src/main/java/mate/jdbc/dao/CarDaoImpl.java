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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS cars_id, cars.model AS cars_model, "
                + "cars.is_deleted AS cars_is_deleted, "
                + "manufacturers.id AS manufacturers_id, "
                + "manufacturers.name AS manufacturers_name, "
                + "manufacturers.country AS manufacturers_country, "
                + "manufacturers.is_deleted AS manufacturers_is_deleted "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id"
                + "WHERE cars_id = ? AND cars_is_deleted = false "
                + "AND manufacturers.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(selectDrivers(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, "
                + "cars.is_deleted, "
                + "manufacturers.id, "
                + "manufacturers.name, "
                + "manufacturers.country, "
                + "manufacturers.is_deleted "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = false AND manufacturers.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(selectDrivers(car)));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB", e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id AS cars_id, cars.model AS cars_model, "
                + "manufacturer.id AS manufacturer_id, manufacturer.name AS manufacturer_name,"
                + "manufacturer.country AS manufacturer_country "
                + "FROM cars_drivers "
                + "JOIN cars ON cars_drivers.car_id = cars.id "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars_drivers.driver_id = ? AND cars.is_deleted = false "
                + "AND manufacturers.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars for " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(selectDrivers(car)));
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        final Long carId = resultSet.getObject("cars.id", Long.class);
        final String model = resultSet.getString("cars.model");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers.id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        return new Car(carId, model, manufacturer);
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't save drivers for " + car, e);
        }
    }

    private List<Driver> selectDrivers(Car car) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for " + car, e);
        }
        return drivers;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void deleteDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver for " + car, e);
        }
    }
}
