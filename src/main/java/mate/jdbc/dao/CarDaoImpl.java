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
        String insertRequest =
                "INSERT INTO cars (model,manufacturer_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(
                             insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB", e);
        }

        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery =
                "SELECT c.id as car_id, model, m.id, m.name,m.country"
                        + " FROM CARS c JOIN manufacturers m ON c.manufacturer_id = m.id"
                        + " WHERE c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(
                                getQuery, Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, id);
            getCarStatement.executeUpdate();
            ResultSet resultSet = getCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car = getCar (resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }

        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS id, m.id AS manufacturer_id, c.model, m.name, m.country"
                + " FROM cars c JOIN manufacturers m ON m.id = c.manufacturer_id"
                + " WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar (resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars.", e);
        }
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        deleteDriversFromCar(car.getId());
        addDriversToCar(car);
        String updateQuery = "UPDATE cars"
                + " SET model = ?,manufacturer_id = ?"
                + " WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(
                        updateQuery, Statement.NO_GENERATED_KEYS)) {
            updateStatement.setLong(3, car.getId());
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createFormatsStatement =
                        connection.prepareStatement(
                                deleteQuery, Statement.RETURN_GENERATED_KEYS)) {
            createFormatsStatement.setLong(1, id);
            return createFormatsStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("can't update table in db by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String allCarByDriverIdQuery = "SELECT c.id,m.id "
                + "AS manufacturer_id,c.model,m.country,m.name "
                + "FROM cars_drivers ca "
                + "JOIN cars c ON c.id = ca.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE ca.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(
                             allCarByDriverIdQuery, Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, driverId);
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar (resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by driver_id " + driverId, e);
        }
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id, d.name, d.license_number FROM drivers d"
                + " INNER JOIN cars_drivers cd"
                + " ON cd.driver_id = d.id WHERE car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id: " + id, e);
        }
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createDriversStatement =
                        connection.prepareStatement(query)) {
            createDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createDriversStatement.setLong(2, driver.getId());
                createDriversStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataProcessingException(
                    "Can't create drivers for car: " + car, e);
        }
    }

    private void deleteDriversFromCar(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement =
                        connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete drivers from car with id: " + id, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car getCar (ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }
}
