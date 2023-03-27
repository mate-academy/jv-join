package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final int PARAMETER_INDEX_1 = 1;
    private static final int PARAMETER_INDEX_2 = 2;
    private static final int PARAMETER_INDEX_3 = 3;
    private static final int COLUMN_INDEX_1 = 1;
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String LICENSE_NUMBER_COLUMN = "license_number";
    private static final String MODEL_COLUMN = "model";
    private static final String MANUFACTURER_ID_COLUMN = "manufacturer_id";

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(PARAMETER_INDEX_1, car.getModel());
            statement.setLong(PARAMETER_INDEX_2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(COLUMN_INDEX_1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        recordDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery =
                "SELECT id, model, manufacturer_id FROM"
                       + " cars WHERE id = ? AND is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection
                         .prepareStatement(getCarQuery)) {
            getCarStatement.setLong(PARAMETER_INDEX_1, id);
            ResultSet resultSet = getCarStatement.executeQuery();;
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversById(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT * FROM cars WHERE is_deleted = FALSE";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection
                         .prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from DB", e);
        }
        allCars.forEach(c -> c.setDrivers(getDriversById(c.getId())));
        return allCars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverQuery = "SELECT id, model, manufacturer_id "
                + "FROM cars c JOIN cars_drivers cd "
                + "ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.is_deleted = FALSE";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarsStatement = connection
                         .prepareStatement(getCarsByDriverQuery)) {
            getCarsStatement.setLong(PARAMETER_INDEX_1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by id " + driverId, e);
        }
        allCars.forEach(c -> c.setDrivers(getDriversById(c.getId())));
        return allCars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ?  "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement = connection
                         .prepareStatement(updateQuery)) {
            updateStatement.setString(PARAMETER_INDEX_1, car.getModel());
            updateStatement.setLong(PARAMETER_INDEX_2, car.getManufacturer().getId());
            updateStatement.setLong(PARAMETER_INDEX_3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteRelation(car.getId());
        recordDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement = connection
                         .prepareStatement(deleteQuery)) {
            deleteStatement.setLong(PARAMETER_INDEX_1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    private void deleteRelation(Long carId) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement = connection
                         .prepareStatement(deleteQuery)) {
            deleteStatement.setLong(PARAMETER_INDEX_1, carId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relation by carId " + carId, e);
        }
    }

    private List<Driver> getDriversById(Long carId) {
        String driverQuery = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriverStatement = connection
                             .prepareStatement(driverQuery)) {
            getDriverStatement.setLong(PARAMETER_INDEX_1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver by id " + carId, e);
        }
    }

    private void recordDrivers(Car car) {
        String driversQuery = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(driversQuery)) {
            statement.setLong(PARAMETER_INDEX_2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(PARAMETER_INDEX_1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(ID_COLUMN, Long.class);
        String model = resultSet.getString(MODEL_COLUMN);
        Long manufacturerId = resultSet.getObject(MANUFACTURER_ID_COLUMN, Long.class);
        ManufacturerDao manufacturer = new ManufacturerDaoImpl();
        return new Car(id, model, manufacturer.get(manufacturerId).get());
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(ID_COLUMN, Long.class);
        String name = resultSet.getString(NAME_COLUMN);
        String licenseNumber = resultSet.getString(LICENSE_NUMBER_COLUMN);
        return new Driver(id, name, licenseNumber);
    }
}
