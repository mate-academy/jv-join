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
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement =
                         connection.prepareStatement(createRequest,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT cars.id, model, manufacturer_id, "
                + "manufacturers.name AS mark, country FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = "
                + "manufacturers.id WHERE cars.is_deleted = FALSE AND cars.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement =
                             connection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB with id= " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT cars.id, model, manufacturer_id, "
                + "manufacturers.name AS mark, country FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = "
                + "manufacturers.id WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllStatement =
                         connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't read all cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement =
                         connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car, e);
        }
        removeDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement deleteStatement =
                             connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB with id= " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllDriverRequest = "SELECT cars.id, model, manufacturer_id, name "
                + "AS mark, country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars.is_deleted = FALSE "
                + "AND manufacturers.is_deleted = FALSE AND driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriverStatement =
                         connection.prepareStatement(getAllDriverRequest)) {
            getAllDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars with driver_id= " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertStatement =
                         connection.prepareStatement(insertRequest)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver to car " + car, e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("mark"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriverForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers JOIN cars_drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE drivers.is_deleted = FALSE AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatement =
                         connection.prepareStatement(getDriverForCarRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers from DB for car with id= "
                    + id, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void removeDrivers(Long id) {
        String removeRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeStatement =
                         connection.prepareStatement(removeRequest)) {
            removeStatement.setLong(1, id);
            removeStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations with id= " + id, e);
        }
    }
}
