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
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    private static final String CREATE_CAR_QUERY = "INSERT INTO taxi_service.cars"
            + " (model, manufacturer_id) VALUES (?, ?)";
    private static final String GET_CAR_BY_ID_QUERY = "SELECT id, model,"
            + " manufacturer_id FROM cars WHERE id = ? AND is_deleted = FALSE";
    private static final String GET_ALL_CAR_QUERY = "SELECT * FROM cars WHERE is_deleted = FALSE";
    private static final String UPDATE_CAR_QUERY = "UPDATE cars "
            + "SET model = ?, manufacturer_id = ? "
            + "WHERE id = ? AND is_deleted = FALSE";
    private static final String SOFT_DELETE_CAR_QUERY = "UPDATE cars"
            + " SET is_deleted = TRUE WHERE id = ?";
    private static final String GET_ALL_BY_DRIVER_ID_QUERY = "SELECT * FROM cars AS"
            + " c JOIN cars_drivers AS cd"
            + " ON c.id = cd.car_id WHERE cd.driver_id = ?";
    private static final String ADD_DRIVER_TO_CARS_DRIVERS_QUERY = "INSERT INTO cars_drivers"
            + " (driver_id, car_id) VALUES (?, ?)";
    private static final String REMOVE_DRIVER_FROM_CAR_QUERY = "DELETE FROM "
            + "cars_drivers WHERE car_id = driver_id AND driver_id = ?";
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(CREATE_CAR_QUERY,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", exception);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_CAR_BY_ID_QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCar(resultSet));
            }
            throw new DataProcessingException("Couldn't get car by id " + id);
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't get car by id " + id, exception);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_ALL_CAR_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't get"
                    + " a list of cars from carsDB.", exception);
        }
    }

    @Override
    public Car update(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(UPDATE_CAR_QUERY)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            return car;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", exception);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(SOFT_DELETE_CAR_QUERY)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't delete car with id " + id, exception);
        }
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(ADD_DRIVER_TO_CARS_DRIVERS_QUERY)) {
            statement.setLong(1, driver.getId());
            statement.setLong(2, car.getId());
            statement.executeUpdate();
            car.getDrivers().add(driver);
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't insert values"
                    + " into cars_drivers db", exception);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().remove(driver);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(REMOVE_DRIVER_FROM_CAR_QUERY)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't remove driver from car", exception);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carsByDriver = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(GET_ALL_BY_DRIVER_ID_QUERY)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsByDriver.add(getCar(resultSet));
            }
            return carsByDriver;
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't get all"
                    + " cars by driver id " + driverId, exception);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = manufacturerDao.get(id).get();
        return new Car(id, model, manufacturer, null);
    }
}
