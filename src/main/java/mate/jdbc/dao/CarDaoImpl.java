package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final int INDEX_COLUMN_ONE = 1;
    private static final int INDEX_COLUMN_TWO = 2;
    private static final int INDEX_COLUMN_THREE = 3;

    @Override
    public Car create(Car car) {
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(INDEX_COLUMN_ONE, car.getModel());
            createCarStatement.setLong(INDEX_COLUMN_TWO, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(INDEX_COLUMN_ONE, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB. Car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                    connection.prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(INDEX_COLUMN_ONE, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(INDEX_COLUMN_TWO, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car. Car: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
