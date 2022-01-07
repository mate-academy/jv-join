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
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer_id());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        insertDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT id, model, manufacturer_id "
                + "FROM cars "
                + "WHERE id = ? AND is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarsFromResultSet(resultSet);
            }
            car.setDrivers(getDriversForCar(id));
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT id, model, manufacturer_id "
                + "FROM cars "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer_id());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in driversDB.", e);
        }
        deleteAllDriversFromCar(car.getId());
        insertDriversToCar(car);
        return car;
    }

    public List<Long> getAllDriversIdByCar(Long carId) {
        String getAllOriversForCar = "SELECT driver_id FROM cars_drivers "
                + "WHERE car_id = ?;";
        List<Long> ids = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllOriversForCar)) {
            getAllCarsStatement.setLong(1, carId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                ids.add(resultSet.getObject("driver_id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers in DB by car id=" + carId, e);
        }
        return ids;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsForDriverRequest = "SELECT id, model, manufacturer_id "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllCarsForDriverRequest)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find cars in DB by driver id=" + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers in DB for car id=" + carId, e);
        }
        return drivers;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("drivers.id", Long.class));
        driver.setName(resultSet.getString("drivers.name"));
        driver.setLicenseNumber(resultSet.getString("drivers.license_number"));
        return driver;
    }

    private void deleteAllDriversFromCar(Long id) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ? AND driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            List<Long> driversId = new ArrayList<>();
            driversId = getAllDriversIdByCar(id);
            for (Long driverId : driversId) {
                deleteDriversStatement.setLong(2, driverId);
                deleteDriversStatement.execute();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers from car №" + id, e);
        }
    }

    private void insertDriversToCar(Car car) {
        String insertDriversRequest = "INSERT INTO `cars_drivers` "
                + "(car_id, driver_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert drivers to " + car, e);
        }
    }

    private Car parseCarsFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("cars.id", Long.class));
        car.setModel(resultSet.getString("cars.model"));
        car.setManufacturer_id(resultSet.getObject("cars.manufacturer_id", Long.class));
        return car;
    }
}
