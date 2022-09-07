package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final String GET_ALL_QUERY = "SELECT cars.id car_id, cars.model, "
            + "m.id manufacturer_id, m.name manufacturer_name, m.country, "
            + "drivers.id driver_id, drivers.name driver_name, drivers.license_number "
            + "FROM cars "
            + "INNER JOIN manufacturers AS m "
            + "ON cars.manufacturer_id = m.id "
            + "LEFT JOIN cars_drivers "
            + "ON cars_drivers.car_id = cars.id "
            + "LEFT JOIN "
            + "(SELECT * FROM drivers WHERE is_deleted = FALSE) AS drivers "
            + "ON cars_drivers.driver_id = drivers.id "
            + "WHERE cars.is_deleted = FALSE";

    @Override
    public Car create(Car car) {
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
            throw new DataProcessingException("Couldn't create car in DB. Car: " + car, e);
        }
        insertDriversByCarInDB(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String carByIdQuery = GET_ALL_QUERY + " AND cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(carByIdQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id=" + id + " from DB.", e);
        }
    }

    @Override
    public List<Car> getAll() {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Car, List<Driver>> driversMapByCar = new HashMap<>();
            while (resultSet.next()) {
                fillMap(driversMapByCar, resultSet);
            }
            return retrieveCarsFromMap(driversMapByCar);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
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
            throw new DataProcessingException("Couldn't update car in DB. Car: " + car, e);
        }
        deleteDriversByCarInDB(car);
        insertDriversByCarInDB(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id=" + id + " in DB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String carsByDriverQuery = GET_ALL_QUERY + " AND drivers.id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(carsByDriverQuery)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get a list of cars by driver id=" + driverId + " from DB.", e);
        }
        String driversByCarQuery = GET_ALL_QUERY + " AND cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(driversByCarQuery)) {
            Map<Car, List<Driver>> driversMapByCar = new HashMap<>();
            for (Car car : cars) {
                statement.setLong(1, car.getId());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    fillMap(driversMapByCar, resultSet);
                }
            }
            return retrieveCarsFromMap(driversMapByCar);
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get a list of drivers from DB.", e);
        }
    }

    private void insertDriversByCarInDB(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't insert a list of drivers into DB for car: " + car, e);
        }
    }

    private void deleteDriversByCarInDB(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete a list of drivers in DB for car: " + car, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = parseCarFromResultSet(resultSet);
        List<Driver> drivers = new ArrayList<>();
        do {
            if (isDriverId(resultSet)) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } while (resultSet.next());
        car.setDrivers(drivers);
        return car;
    }

    private void fillMap(Map<Car, List<Driver>> driversMapByCar,
                         ResultSet resultSet) throws SQLException {
        Car car = parseCarFromResultSet(resultSet);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = parseDriverFromResultSet(resultSet);
        if (driversMapByCar.containsKey(car)) {
            if (isDriverId(resultSet)) {
                driversMapByCar.get(car).add(driver);
            }
            return;
        }
        if (isDriverId(resultSet)) {
            drivers.add(driver);
        }
        driversMapByCar.put(car, drivers);
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("manufacturer_name"),
                resultSet.getString("country"));
        return new Car(id, model, manufacturer);
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("driver_name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private boolean isDriverId(ResultSet resultSet) throws SQLException {
        return resultSet.getObject("driver_id") != null;
    }

    private List<Car> retrieveCarsFromMap(Map<Car, List<Driver>> driversMapByCar) {
        driversMapByCar.forEach(Car::setDrivers);
        List<Car> cars = new ArrayList<>(driversMapByCar.keySet());
        Collections.sort(cars);
        return cars;
    }
}
