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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCarStatement =
                        connection.prepareStatement(insertDriversQuery)) {
            insertDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversToCarStatement.setLong(2, driver.getId());
                insertDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers for car: "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS car_id, model, m.id "
                + "AS manufacturer_id, m.name, m.country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car carFromDB = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                carFromDB = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        return Optional.ofNullable(carFromDB);
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = Manufacturer.builder()
                .id(resultSet.getObject("manufacturer_id", Long.class))
                .name(resultSet.getString("name"))
                .country(resultSet.getString("country"))
                .build();
        List<Driver> drivers = getDriversForCar(resultSet.getObject("car_id", Long.class));
        return Car.builder()
                .id(resultSet.getObject("car_id", Long.class))
                .model(resultSet.getString("model"))
                .manufacturer(manufacturer)
                .drivers(drivers)
                .build();
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarQuery = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND d.is_deleted = FALSE";
        List<Driver> driversForCar = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getAllDriversForCarQuery)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driversForCar.add(Driver.builder()
                        .id(resultSet.getObject("id", Long.class))
                        .name(resultSet.getString("name"))
                        .licenseNumber(resultSet.getString("license_number"))
                        .build());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car by car_id " + carId, e);
        }
        return driversForCar;
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id, model, m.id "
                + "AS manufacturer_id, m.name, m.country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + " in DB.", e);
        }
        deleteAllRelationsBetweenCarAndDriversIfExists(car);
        insertDrivers(car);
        return car;
    }

    private void deleteAllRelationsBetweenCarAndDriversIfExists(Car car) {
        String deleteRelationsQuery = "DELETE cars_drivers.* FROM cars_drivers "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteRelationsQuery)) {
            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete relations between drivers and car "
                    + "with id " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteStatement =
                        connection.prepareStatement(softDeleteQuery)) {
            softDeleteStatement.setLong(1, id);
            return softDeleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverQuery = "SELECT DISTINCT(c.id) AS car_id, "
                + "model, m.id AS manufacturer_id, m.name, m.country "
                + "FROM taxi.cars c JOIN taxi.manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN taxi.cars_drivers cd ON cd.car_id = c.id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement
                        = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driverId: "
                    + driverId, e);
        }
    }
}
