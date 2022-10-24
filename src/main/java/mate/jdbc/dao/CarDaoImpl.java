package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(final Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
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
            throw new DataProcessingException("Couldn't create car: " + car + ".", e);
        }
        if (car.getId() != null) {
            addDrivers(car.getId(), car.getDrivers());
        }
        return car;
    }

    @Override
    public Optional<Car> get(final Long carId) {
        Optional<Car> carResult = Optional.empty();
        String query = "SELECT c.id AS car_id, model AS car_model, manufacturer_id AS m_id, " +
                "name AS m_name, country AS m_country FROM cars c " +
                "JOIN manufacturers m " +
                "ON c.manufacturer_id = m.id " +
                "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                Car car = new Car();
                car.setId(carId);
                car.setModel(resultSet.getString(2));
                car.setManufacturer(generateManufacturer(resultSet));
                carResult = Optional.of(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id: " + carId + ".", e);
        }
        carResult.ifPresent(car -> car.setDrivers(getAllDriversByCarId(carId)));
        return carResult;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id, manufacturers.name, manufacturers.country " +
                "FROM cars " +
                "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id " +
                "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                cars.add(generateCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB.", e);
        }
        if (cars.size() > 0) {
            cars.forEach(c -> c.setDrivers(getAllDriversByCarId(c.getId())));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(final Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id AS car_id, c.model AS car_model, c.manufacturer_id AS manufacturer_id, " +
                "m.name  AS manufacturer_name, m.country AS manufacturer_country " +
                "FROM cars c " +
                "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id " +
                "RIGHT JOIN cars_drivers cd ON c.id = cd.car_id " +
                "WHERE cd.is_deleted = FALSE AND cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                cars.add(generateCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id from DB. id: " + driverId + ".", e);
        }
        if (cars.size() > 0) {
            cars.forEach(c -> c.setDrivers(getAllDriversByCarId(c.getId())));
        }
        return cars;
    }

    @Override
    public Car update(final Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? " +
                "WHERE id = ? " +
                "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car:" + car + ".", e);
        }
        deleteDrivers(car.getId());
        addDrivers(car.getId(), car.getDrivers());
        return car;
    }

    @Override
    public boolean delete(final Long carId) {
        String query = "UPDATE cars SET is_deleted = TRUE" +
                "WHERE id = ?;";
        int updatedRows;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            updatedRows = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id:" + carId + ".", e);
        }
        if (updatedRows > 0) {
            deleteDrivers(carId);
        }
        return updatedRows > 0;
    }

    private List<Driver> getAllDriversByCarId(final Long id) {
        String getQuery = "SELECT d.id, d.name, d.license_number FROM drivers d " +
                "JOIN cars_drivers cd " +
                "ON cd.driver_id = d.id " +
                "WHERE cd.car_id = ? AND cd.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getQuery)) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                Driver driver = new Driver(
                        resultSet.getObject(1, Long.class),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car id: " + id + ".", e);
        }
        return drivers;
    }

    private Car generateCar(final ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));
        car.setManufacturer(generateManufacturer(resultSet));
        return car;
    }

    private Manufacturer generateManufacturer(final ResultSet resultSet) throws SQLException {
        return new Manufacturer(
                resultSet.getObject(3, Long.class),
                resultSet.getString(4),
                resultSet.getString(5)
        );
    }

    private void addDrivers(final Long carId, final List<Driver> drivers) {
        String createQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(createQuery)) {
            for (final Driver driver : drivers) {
                createCarStatement.setLong(1, carId);
                createCarStatement.setLong(2, driver.getId());
                createCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add drivers by car id:" + carId + ".", e);
        }
    }

    private void deleteDrivers(final Long carId) {
        String updateQuery = "UPDATE cars_drivers SET is_deleted = TRUE " +
                "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(updateQuery)) {
            createCarStatement.setLong(1, carId);
            createCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers by car id:" + carId + ".", e);
        }
    }
}
