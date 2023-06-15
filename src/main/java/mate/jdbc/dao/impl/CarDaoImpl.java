package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    @Override
    public Car create(Car car) {
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(createQuery,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create a car: " + car, e);
        }
        insertDriversForCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.model as car_model, c.id as car_id, manufacturer_id, "
                          + "m.name as manufacturer_name, m.country as manufacturer_country "
                          + "FROM cars c "
                          + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                          + "WHERE c.is_deleted = FALSE  AND c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.model as car_model, c.id as car_id, manufacturer_id, "
                             + "m.name as manufacturer_name, m.country as manufacturer_country "
                             + "FROM cars c "
                             + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                             + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                             + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car: " + car, e);
        }
        removeAllDriversFromCar(car.getId());
        insertDriversForCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        boolean isCarDeleted;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            isCarDeleted = deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car with id: " + id, e);
        }
        removeAllDriversFromCar(id);
        return isCarDeleted;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.model as car_model, c.id as car_id, "
                                     + "manufacturer_id, m.name as manufacturer_name, "
                                     + "m.country as manufacturer_country "
                                     + "FROM cars c "
                                     + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                                     + "JOIN cars_drivers cd ON cd.car_id = c.id "
                                     + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by a driver ID: " + driverId, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        car.setManufacturer(parseManufacturer(resultSet));
        car.setDrivers(getDriversByCar(car.getId()));
        return car;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        return manufacturer;
    }

    private List<Driver> getDriversByCar(Long carId) {
        String getDriversQuery = "SELECT id, name, license_number "
                                 + "FROM drivers d "
                                 + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                                 + "WHERE cd.car_id = ? ORDER BY id ASC;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getDriversQuery)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for a car with id: " + carId, e);
        }
    }

    private void removeAllDriversFromCar(Long carId) {
        String removeQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeAllDriversStatement =
                        connection.prepareStatement(removeQuery)) {
            removeAllDriversStatement.setLong(1, carId);
            removeAllDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove all drivers from car with id: "
                                              + carId, e);
        }
    }

    private void insertDriversForCar(Car car) {
        for (Driver driver : car.getDrivers()) {
            insertDriver(car.getId(), driver.getId());
        }
    }

    private void insertDriver(Long carId, Long driverId) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, carId);
            insertDriversStatement.setLong(2, driverId);
            insertDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver fom car with id: "
                                              + carId, e);
        }
    }
}
