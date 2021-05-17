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
        String createRequest = "INSERT INTO cars "
                + "(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't create new car in DB with input params. Car: " + car, e);
        }
        if (car != null) {
            insertDrivers(car);
        }
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers(cars_id, drivers_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(2, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers.", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT * "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE "
                + "AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = createCarObjectWithResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Car from DB. ID = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversById(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarRequest = "SELECT * "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "   ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet = getAllStatement.executeQuery(getAllCarRequest);
            while (resultSet.next()) {
                Car car = createCarObjectWithResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB.", e);
        }
        cars.stream()
                .forEach(n -> n.setDrivers(getDriversById(n.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarField = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateFieldStatement =
                        connection.prepareStatement(updateCarField)) {
            updateFieldStatement.setString(1, car.getModel());
            updateFieldStatement.setLong(2, car.getManufacturer().getId());
            updateFieldStatement.setLong(3, car.getId());
            updateFieldStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in DB. Car: " + car, e);
        }
        deleteDriversRelations(car.getId());
        createNewDriversRelations(car);
        return car;
    }

    private void deleteDriversRelations(Long carId) {
        String deleteRelations = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteRelations)) {
            deleteRelationsStatement.setLong(1, carId);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car and "
                    + "drivers relations by id = " + carId, e);
        }
    }

    private void createNewDriversRelations(Car car) {
        String createRelations = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationsStatement =
                        connection.prepareStatement(createRelations)) {
            createRelationsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createRelationsStatement.setLong(2, driver.getId());
                createRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create drivers for car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB with id = " + id, e);
        }
    }

    private Car createCarObjectWithResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        car.setId(carId);
        car.setModel(model);

        Manufacturer manufacturer = new Manufacturer();
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        manufacturer.setCountry(manufacturerCountry);
        manufacturer.setId(manufacturerId);
        manufacturer.setName(manufacturerName);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversById(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getDriversRequest = "SELECT * "
                + "FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.drivers_id "
                + "WHERE cd.cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                         connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(createDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id = " + carId, e);
        }
    }

    private Driver createDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }
}
