package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        car.getDrivers().forEach(d -> addLink(car.getId(), d.getId()));
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarQuery = "SELECT c.id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "LEFT JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Optional<Car> car = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectCarQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = Optional.of(extractCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.ifPresent(c -> c.setDrivers(queryDrivers(id)));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country"
                + "FROM cars c "
                + "LEFT JOIN manufacturers m ON m.id = c.manufacturer_id"
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(extractCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        cars.forEach(c -> c.setDrivers(queryDrivers(c.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        mergeCarDrivers(car.getId(), car.getDrivers());
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        Integer deleteResult = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            deleteResult = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id "
                    + id, e);
        }
        queryDrivers(id).forEach(d -> removeLink(id, d.getId()));
        return deleteResult > 0;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars_drivers cd "
                + "LEFT JOIN cars c on c.id = cd.car_id "
                + "LEFT JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted IS FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(extractCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    public Set<Driver> queryDrivers(Long carId) {
        String query = "SELECT d.* FROM cars_drivers cd "
                + "LEFT JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ? "
                + "AND d.is_deleted = FALSE;";
        Set<Driver> drivers = new HashSet<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(extractDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers from driversDB.", e);
        }
    }

    private Driver extractDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private Car extractCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getObject("name", String.class),
                resultSet.getObject("country", String.class));
        return new Car(resultSet.getObject("id", Long.class),
                manufacturer,
                resultSet.getObject("model", String.class));
    }

    private void mergeCarDrivers(Long carId, Set<Driver> newDrivers) {
        final Set<Driver> existing = queryDrivers(carId);
        final Set<Driver> toRemove = existing.stream()
                .filter(o -> !newDrivers.contains(o))
                .collect(Collectors.toSet());
        final Set<Driver> toAdd = newDrivers.stream()
                .filter(o -> !existing.contains(o))
                .collect(Collectors.toSet());
        toRemove.forEach(d -> removeLink(carId, d.getId()));
        toAdd.forEach(a -> addLink(carId, a.getId()));
    }

    private void removeLink(Long carId, Long driverId) {
        final String query = "DELETE "
                + "FROM cars_drivers "
                + "WHERE car_id = ? "
                + "AND driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.setLong(2, driverId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete link with car_id " + carId
                            + " and driver_id = " + driverId, e);
        }
    }

    private void addLink(Long carId, Long driverId) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.setLong(2, driverId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't create link for car_id = "
                            + carId + " and driver_id = "
                            + driverId, e);
        }
    }
}
