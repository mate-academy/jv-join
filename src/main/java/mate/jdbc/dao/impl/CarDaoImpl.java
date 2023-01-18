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
        String carGetQuery = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement((carGetQuery),
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String carGetQuery = "SELECT c.id as car_id, model, c.is_deleted, "
                + "m.id as manufacturer_id, name, country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(carGetQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, name, country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of all cars", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars table.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, model,"
                + " m.id AS manufacturer_id, name, country "
                + "FROM cars_drivers cdr "
                + "JOIN cars c "
                + "ON cdr.car_id = c.id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ?"
                + " AND c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driver id " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversByCarId(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers(car_id, driver_id)"
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertDriversQuery)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car " + car, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country")
        );
        return new Car(id, model, manufacturer);
    }

    private List<Driver> getAllDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers a JOIN cars_drivers b ON a.id = b.driver_id "
                + "WHERE b.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllDriversForCarRequest)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            if (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private List<Driver> getDriversByCarId(Long id) {
        String query = "SELECT id, name, license_number "
                + "FROM cars_drivers cdr "
                + "JOIN drivers d "
                + "ON cdr.driver_id = d.id "
                + "WHERE d.is_deleted = FALSE AND car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by car id " + id, e);
        }
    }
}
