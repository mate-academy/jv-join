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
    private static final String CAR_ID_COLUMN = "id";
    private static final String CAR_MODEL_COLUMN = "model";
    private static final String CAR_MANUFACTURER_ID_COLUMN = "manufacturer_id";
    private static final String MANUFACTURER_NAME_COLUMN = "name";
    private static final String MANUFACTURER_COUNTRY_COLUMN = "country";
    private static final String DRIVER_ID_COLUMN = "id";
    private static final String DRIVER_NAME_COLUMN = "name";
    private static final String DRIVER_LICENSE_NUMBER_COLUMN = "license_number";

    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                        connection.prepareStatement(
                                createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id, c.model, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE and c.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllFromCarsTableQuery = "SELECT *"
                + "FROM cars c JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllFromCarsTableQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
            cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.id, "
                + "c.model, m.id AS manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
            cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driverId " + driverId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteCarFromCarsDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        int numberOfDeletedRows = 0;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            numberOfDeletedRows = deleteCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete the car by id " + id, e);
        }
        deleteCarFromCarsDrivers(id);
        return numberOfDeletedRows != 0;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCarStatement =
                        connection.prepareStatement(insertDriversQuery)) {
            for (Driver driver : car.getDrivers()) {
                insertDriversToCarStatement.setLong(1, car.getId());
                insertDriversToCarStatement.setLong(2, driver.getId());
                insertDriversToCarStatement.addBatch();
            }
            insertDriversToCarStatement.executeBatch();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(CAR_ID_COLUMN, Long.class));
        car.setModel(resultSet.getString(CAR_MODEL_COLUMN));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(CAR_MANUFACTURER_ID_COLUMN, Long.class));
        manufacturer.setName(resultSet.getString(MANUFACTURER_NAME_COLUMN));
        manufacturer.setCountry(resultSet.getString(MANUFACTURER_COUNTRY_COLUMN));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriverQuery = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = FALSE AND cd.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                            connection.prepareStatement(getDriverQuery)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get the driver by carId " + carId, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject(DRIVER_ID_COLUMN, Long.class),
                resultSet.getString(DRIVER_NAME_COLUMN),
                resultSet.getString(DRIVER_LICENSE_NUMBER_COLUMN)
        );
    }

    private void deleteCarFromCarsDrivers(Long carId) {
        String deleteCarIdFromCarsDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarIdFromCarsDriversStatement =
                        connection.prepareStatement(deleteCarIdFromCarsDriversQuery)) {
            deleteCarIdFromCarsDriversStatement.setLong(1, carId);
            deleteCarIdFromCarsDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete carId " + carId
                    + " from cars_drivers table", e);
        }
    }
}
