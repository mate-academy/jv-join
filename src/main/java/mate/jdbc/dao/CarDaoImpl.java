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
        String insertRequest = "INSERT INTO cars (title, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                        = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, car.getTitle());
            insertStatement.setLong(2, car.getManufacturer().getId());
            insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create a car: " + car, throwable);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.title, c.manufacturer_id, m.title, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getCarStatement
                                = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = new Car();
                car.setId(id);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
                car.setTitle(resultSet.getString(2));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversById(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.title, c.manufacturer_id, m.title, m.country "
                + "FROM cars c JOIN manufacturer m ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement getDriverStatement
                    = connection.prepareStatement(query);
            ResultSet resultSet = getDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
                car.setTitle(resultSet.getString(2));
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars.", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversById(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET title = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement(updateCarQuery)) {
            statement.setString(1, car.getTitle());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car by id - " + car.getId(), e);
        }
        deleteDriversByCar(car);
        addDriversToCar(car);
        return car;
    }

    private boolean deleteDriversByCar(Car car) {
        String deleteAllDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement
                            = connection.prepareStatement(deleteAllDriversQuery)) {
            statement.setLong(1, car.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't deleted drivers by car " + car, e);
        }
    }

    private Car addDriversToCar(Car car) {
        String insertDriversQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement
                            = connection.prepareStatement(
                                    insertDriversQuery)) {
            statement.setLong(1, car.getId());
            if (car.getDrivers() == null) {
                return car;
            }
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car " + car, e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteCarStatement
                                = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectDrivers = "SELECT c.id, c.title, manufacturer_id,"
                + "m.title, m.country"
                + " FROM cars c JOIN cars_drivers ON "
                + "id = car_id"
                + " JOIN manufacturers m ON "
                + "manufacturer_id = m.id"
                + " WHERE driver_id = ? AND is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement selectStatement
                    = connection.prepareStatement(selectDrivers);
            selectStatement.setLong(1, driverId);
            ResultSet resultSet = selectStatement.executeQuery();
            Car car = new Car();
            while (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
                car.setManufacturer(getManufacturerFromResultSet(resultSet));
                car.setTitle(resultSet.getString(2));
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars by driver.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversById(car.getId()));
        }
        return cars;
    }

    private Manufacturer getManufacturerFromResultSet(ResultSet resultSet)
            throws SQLException {
        Manufacturer manufacturer = new Manufacturer(
                    resultSet.getString("title"),
                    resultSet.getString("country")
            );
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        return manufacturer;
    }

    private List<Driver> getDriversById(Long id) {
        String selectDrivers = "SELECT d.id, d.name, d.license_number"
                + " FROM drivers d JOIN cars_drivers c ON "
                + "c.driver_id = d.id"
                + " WHERE car_id = ? AND d.is_deleted = false";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection
                = ConnectionUtil.getConnection();
                 PreparedStatement selectStatement
                         = connection.prepareStatement(selectDrivers)) {
            selectStatement.setLong(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            Driver driver;
            while (resultSet.next()) {
                driver = new Driver(
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                driver.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't get drivers for id: " + id);
        }
        return drivers;
    }
}
