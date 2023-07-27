package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final String ADD_DRIVERS_FOR_CAR_QUERY =
            "INSERT INTO cars_drivers (`driver_id`, `car_id`) VALUES ";
    private static final String CREATE_CAR_QUERY =
            "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
    private static final String DELETE_CAR_QUERY =
            "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
    private static final String DELETE_DRIVERS_FOR_CAR_QUERY =
            "DELETE FROM cars_drivers WHERE car_id = ?;";
    private static final String GET_ALL_CARS_BY_DRIVER_QUERY =
            "SELECT car_id, model, manufacturer_id, driver_id "
                    + "FROM cars AS c "
                    + "JOIN cars_drivers AS cd "
                    + "ON c.id = cd.car_id "
                    + "WHERE driver_id = ? AND is_deleted = FALSE;";
    private static final String GET_ALL_CARS_QUERY =
            "SELECT id AS car_id, model, manufacturer_id FROM cars "
                    + "WHERE is_deleted = FALSE;";
    private static final String GET_CAR_QUERY =
            "SELECT id AS car_id, model, manufacturer_id FROM cars "
            + "WHERE id = ? AND is_deleted = FALSE;";
    private static final String GET_DRIVERS_FOR_CAR_QUERY =
            "SELECT driver_id, name, license_number FROM drivers d "
                    + "JOIN cars_drivers cd "
                    + "ON d.id = cd.driver_id "
                    + "WHERE cd.car_id = ? AND is_deleted = FALSE;";
    private static final String UPDATE_CAR_QUERY = "UPDATE cars "
            + "SET model = ?, manufacturer_id = ? "
            + "WHERE id = ? AND is_deleted = FALSE;";

    @Inject
    private DriverDao driverDao;
    @Inject
    private ManufacturerDao manufacturerDao;

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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_CAR_QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_ALL_CARS_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars.", e);
        }
        cars.stream().forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", e);
        }
        updateDriverList(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_CAR_QUERY)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(GET_ALL_CARS_BY_DRIVER_QUERY)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get all cars by driver with id:" + driverId + ". ", e);
        }
        cars.stream().forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }

    private void updateDriverList(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(DELETE_DRIVERS_FOR_CAR_QUERY)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update (delete)) "
                    + car + " in cars DB.", e);
        }
        insertDrivers(car);
    }

    private void insertDrivers(Car car) {
        if (!car.getDrivers().isEmpty()) {
            String insertQueryValues = car.getDrivers()
                    .stream()
                    .map(d -> "('" + d.getId() + "', '" + car.getId() + "')")
                    .collect(Collectors.joining(", "));
            try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement
                            = connection.prepareStatement(ADD_DRIVERS_FOR_CAR_QUERY
                            + insertQueryValues + ";")) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataProcessingException("Couldn't update (add)"
                        + car + " in cars DB.", e);
            }
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(GET_DRIVERS_FOR_CAR_QUERY)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car id " + carId, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(
                manufacturerDao.get(
                                resultSet.getObject("manufacturer_id", Long.class))
                        .get());
        return car;
    }
}
