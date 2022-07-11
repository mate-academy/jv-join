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
        String createCarRequest = "INSERT into cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create Car: " + car, e);
        }
        addDriversToCarsDriversTable(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest =
                "SELECT c.id AS car_id, c.model, c.manufacturer_id "
                + "FROM cars c "
                + "WHERE c.id = ? "
                + "AND c.is_deleted = 0 ";
        Car car = null;
        ResultSet resultSet;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = new Car();
                car.setId(id);
                car.setModel(resultSet.getString("model"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Car with id: " + id, e);
        }
        if (car != null) {
            car.setManufacturer(getManufacturerForCar(car.getId()));
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest =
                "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, "
                + "m.name as manufacturer_name, m.country as manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = 0 ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                car.setManufacturer(parseManufacturerFromResultSet(resultSet));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all Cars", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String createCarRequest =
                "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE cars.id = ? "
                + "AND is_deleted = 0 ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update Car: " + car, e);
        }
        deleteRelationWithCar(car);
        addDriversToCarsDriversTable(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String createCarRequest =
                "UPDATE cars "
                + "SET is_deleted = 1 "
                + "WHERE cars.id = ? "
                + "AND is_deleted = 0 ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsRequest =
                "SELECT d.id AS driver_id, "
                + "c.id AS car_id, c.model, m.id AS manufacturer_id, "
                + "m.name as manufacturer_name, m.country as manufacturer_country "
                + "FROM drivers d "
                + "JOIN cars_drivers cd ON cd.driver_id = d.id "
                + "JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE d.id = ? "
                + "AND d.is_deleted = 0 ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverRequest = connection.prepareStatement(
                        getAllCarsRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            getAllByDriverRequest.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverRequest.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                car.setManufacturer(parseManufacturerFromResultSet(resultSet));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Cars by Driver with id: " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private void addDriversToCarsDriversTable(Car car) {
        String queryForJoiningTable = "INSERT into cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryForJoiningTable,
                        Statement.RETURN_GENERATED_KEYS)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can' get Drivers for Car: " + car, e);
        }
    }

    private Manufacturer getManufacturerForCar(Long carId) {
        String queryForJoiningTable =
                "SELECT m.id, m.name, m.country "
                + "FROM manufacturers m "
                + "JOIN cars c ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? "
                + "AND m.is_deleted = 0 ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerStatement = connection.prepareStatement(
                        queryForJoiningTable,
                        Statement.RETURN_GENERATED_KEYS)) {
            getManufacturerStatement.setLong(1, carId);
            ResultSet resultSet = getManufacturerStatement.executeQuery();
            Manufacturer manufacturer = new Manufacturer();
            if (resultSet.next()) {
                manufacturer.setId(resultSet.getObject("id", Long.class));
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
            }
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Manufacturer for Car with id: "
                    + carId, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest =
                "SELECT id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? "
                + "AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection.prepareStatement(
                        getAllDriversForCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Drivers for Car with id: " + carId, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private Manufacturer parseManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        return manufacturer;
    }

    private void deleteRelationWithCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot remove relation with car: " + car, e);
        }
    }
}
