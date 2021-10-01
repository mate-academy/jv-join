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
        String createRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(createRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add new car: "
                    + car.toString() + " to cars DB. ", e);
        }
        createCarsDriversRecords(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.*, m.name, m.country FROM cars c "
                + "JOIN manufacturers m "
                + "on c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement
                        = connection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithoutDrivers(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: "
                    + id + " from cars DB. ", e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCarId(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllRequest = "SELECT c.*, m.name, m.country FROM cars c "
                + "JOIN manufacturers m "
                + "on c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllStatement
                        = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithoutDrivers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars"
                    + " from cars DB. ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        boolean isUpdate;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement
                        = connection.prepareStatement(updateRequest)) {
            updateStatement.setLong(1, car.getManufacturer().getId());
            updateStatement.setString(2, car.getModel());
            updateStatement.setLong(3, car.getId());
            isUpdate = updateStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with id: "
                    + car.getId() + " in cars DB. ", e);
        }
        if (isUpdate) {
            deleteAllOldDriversFromCar(car);
            createCarsDriversRecords(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteRequest = "UPDATE cars SET is_deleted = TRUE  "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement softDeleteStatement
                         = connection.prepareStatement(softDeleteRequest)) {
            softDeleteStatement.setLong(1, id);
            return softDeleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id
                    + " from cars DB. ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest
                = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllByDriverStatement
                         = connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithoutDrivers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver_id: " + driverId
                    + " from cars DB. ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCarId(car.getId()));
        }
        return cars;
    }

    private void deleteAllOldDriversFromCar(Car car) {
        String deleteAllOldDriversFromCarRequest
                = "DELETE FROM cars_drivers WHERE driver_id = ? AND car_id = ?;";
        List<Driver> allDriversByCarId = getAllDriversByCarId(car.getId());
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteAllOldDriversFromCarStatement
                        = connection.prepareStatement(deleteAllOldDriversFromCarRequest)) {
            deleteAllOldDriversFromCarStatement.setLong(2, car.getId());
            for (Driver driver : allDriversByCarId) {
                deleteAllOldDriversFromCarStatement.setLong(1, driver.getId());
                deleteAllOldDriversFromCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: "
                    + car + " in DB. ", e);
        }
    }

    private Car parseCarWithoutDrivers(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(parseManufacturer(resultSet));
        return car;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet
                .getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private List<Driver> getAllDriversByCarId(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getAllDriversByCarIdRequest = "SELECT driver_id, name, license_number  "
                + "FROM cars_drivers cd  "
                + "JOIN drivers d "
                + "ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversByCarIdStatement
                        = connection.prepareStatement(getAllDriversByCarIdRequest)) {
            getAllDriversByCarIdStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversByCarIdStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver by car_id: "
                    + carId + " from DB. ", e);
        }
        return drivers;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void createCarsDriversRecords(Car car) {
        String createCarsDrivers = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarsDriversStatement
                         = connection.prepareStatement(createCarsDrivers)) {
            createCarsDriversStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                createCarsDriversStatement.setLong(1, driver.getId());
                createCarsDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add to car drivers: "
                    + car.getDrivers().toString() + " to DB. ", e);
        }
    }
}
