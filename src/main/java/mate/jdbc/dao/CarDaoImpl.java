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
        String createRequest
                = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement = connection.prepareStatement(
                         createRequest, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            addDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create a car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT * "
                + "FROM cars AS c "
                + "INNER JOIN manufacturers AS m "
                + "ON c.manufacturers_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement = connection.prepareStatement(
                         getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            if (car != null) {
                car.setDrivers(getDrivers(car.getId()));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT * "
                + "FROM cars AS c "
                + "INNER JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllStatement = connection.prepareStatement(
                         getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB ", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement = connection.prepareStatement(
                         updateRequest)) {
            updateStatement.setLong(1, car.getId());
            updateStatement.setString(2, car.getModel());
            updateStatement.setLong(3, car.getManufacturer().getId());
            updateStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement = connection.prepareStatement(
                         deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT c.*, m.name, m.country "
                + "FROM cars AS c "
                + "INNER JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id "
                + "INNER JOIN cars_drivers AS cd "
                + "ON c.id = cd.car_id "
                + "WHERE cd.driver_id - ? "
                + "AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllByDriverStatement = connection.prepareStatement(
                         getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete all cars by a driver's id " + driverId, e);
        }
    }

    private void addDrivers(Car car) {
        String addDriversRequest = "INSERT INTO cars_drivers (driver_id, car_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriversStatement = connection.prepareStatement(
                         addDriversRequest, Statement.RETURN_GENERATED_KEYS)) {
            addDriversStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversStatement.setLong(1, driver.getId());
                addDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add all drivers to a car " + car, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDrivers(Long carId) {
        String getDriversRequest = "SELECT d.id, d.name, d.license_number "
                + "FROM drivers AS d"
                + "JOIN cars_drivers AS cd"
                + "ON d.id = cd.driver_id"
                + "WHERE cd.driver_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement = connection.prepareStatement(
                         getDriversRequest)) {
            List<Driver> driversList = new ArrayList<>();
            getDriverStatement.setLong(1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = getDriver(resultSet);
                driversList.add(driver);
            }
            return driversList;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete a list of drivers by a car's Id " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }
}
