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
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final String INSERT_CAR_SQL_CREATE_METHOD
            = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?)";
    private static final String SELECT_CAR_SQL_GET_METHOD
            = "SELECT * FROM cars "
            + "LEFT JOIN manufacturers ON "
            + "cars.manufacturers_id = manufacturers.id "
            + "WHERE cars.id = ? AND cars.is_deleted = false";
    private static final String SELECT_ALL_CAR_SQL
            = "SELECT * FROM cars "
            + "LEFT JOIN manufacturers ON "
            + "cars.manufacturers_id = manufacturers.id "
            + "WHERE cars.is_deleted = FALSE";
    private static final String UPDATE_CAR_STATEMENT_UPDATE_METHOD_QUERY
            = "UPDATE cars SET model = ?, manufacturers_id = ? WHERE id = ?";
    private static final String DELETE_DRIVER_UPDATE_METHOD_QUERY
            = "DELETE FROM cars_drivers WHERE car_id = ?";
    private static final String ADD_DRIVER_UPDATE_METHOD_QUERY
            = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
    private static final String DELETE_QUERY_REMOVE_METHOD
            = "UPDATE cars SET is_deleted = true WHERE id = ?";
    private static final String SELECT_CARS_BY_DRIVER_QUERY_GET_ALL_BY_DRIVER
            = "SELECT c.id, c.model, "
            + "m.id as manufacturer_id, m.name as manufacturer_name "
            + "FROM cars c "
            + "JOIN cars_drivers cd ON c.id = cd.car_id "
            + "JOIN drivers d ON cd.driver_id = d.id "
            + "JOIN manufacturers m ON c.manufacturers_id = m.id "
            + "WHERE d.id = ? AND c.is_deleted = false";
    private static final String SELECT_DRIVERS_FOR_CAR_GET_DRIVERS_FOR_CAR
            = "SELECT d.id, d.name, d.license_number "
            + "FROM drivers d "
            + "JOIN cars_drivers cd ON d.id = cd.driver_id "
            + "WHERE cd.car_id = ? AND d.is_deleted = false";
    private static final String INSERT_DRIVERS_QUERY_CREATE_LIST_DRIVERS
            = "INSERT INTO drivers (name, license_number) "
            + "VALUES (?, ?)";
    private static final String INSERT_CAR_DRIVER_QUERY_CREATE_LIST_DRIVERS
            = "INSERT INTO cars_drivers (car_id, driver_id) "
            + "VALUES (?, ?)";
    private static final String SELECT_DRIVERS_QUERY_GET_LIST_DRIVERS
            = "SELECT drivers.* FROM cars_drivers "
            + "JOIN drivers ON cars_drivers.driver_id = drivers.id "
            + "WHERE cars_drivers.car_id = ?";
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Car create(Car car) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            manufacturerDao.create(car.getManufacturer());
            try (PreparedStatement preparedStatement
                         = connection.prepareStatement(INSERT_CAR_SQL_CREATE_METHOD,
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, car.getModel());
                preparedStatement.setLong(2, car.getManufacturer().getId());
                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    Long carId = resultSet.getObject(1, Long.class);
                    car.setId(carId);
                }
            }
            createListDrivers(car, connection);

        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car to DB. For car: " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            try (PreparedStatement preparedStatement
                         = connection.prepareStatement(SELECT_CAR_SQL_GET_METHOD)) {
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                Car car = null;
                if (resultSet.next()) {
                    car = getCar(resultSet);
                    car.setDrivers(geListDriver(connection, car.getId()));
                }
                return Optional.ofNullable(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Error retrieving car " + "by id = " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            try (PreparedStatement preparedStatement
                         = connection.prepareStatement(SELECT_ALL_CAR_SQL)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                Car car;
                while (resultSet.next()) {
                    car = getCar(resultSet);
                    car.setDrivers(geListDriver(connection, car.getId()));
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Error retrieving cars", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(
                        UPDATE_CAR_STATEMENT_UPDATE_METHOD_QUERY);
                PreparedStatement deleteDriversStatement = connection.prepareStatement(
                        DELETE_DRIVER_UPDATE_METHOD_QUERY);
                PreparedStatement addDriverStatement = connection.prepareStatement(
                        ADD_DRIVER_UPDATE_METHOD_QUERY)) {

            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();

            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();

            for (Driver driver : car.getDrivers()) {
                addDriverStatement.setLong(1, car.getId());
                addDriverStatement.setLong(2, driver.getId());
                addDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement
                        = connection.prepareStatement(DELETE_QUERY_REMOVE_METHOD)) {
            preparedStatement.setLong(1, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Error deleting car: " + "by id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement
                        = connection
                        .prepareStatement(SELECT_CARS_BY_DRIVER_QUERY_GET_ALL_BY_DRIVER)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            cars = getListCars(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Error retrieving cars by driverID: " + driverId, e);
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long carId) throws SQLException {
        List<Driver> drivers = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement
                        = connection
                        .prepareStatement(SELECT_DRIVERS_FOR_CAR_GET_DRIVERS_FOR_CAR)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long driverId = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                Driver driver = new Driver(driverId, name, licenseNumber);
                drivers.add(driver);
            }
        }
        return drivers;
    }

    private void createListDrivers(Car car, Connection connection) throws SQLException {

        try (PreparedStatement preparedStatement
                     = connection
                .prepareStatement(INSERT_DRIVERS_QUERY_CREATE_LIST_DRIVERS,
                Statement.RETURN_GENERATED_KEYS);
                PreparedStatement carDriverStmt
                        = connection
                        .prepareStatement(INSERT_CAR_DRIVER_QUERY_CREATE_LIST_DRIVERS)) {
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setString(1, driver.getName());
                preparedStatement.setString(2, driver.getLicenseNumber());
                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    Long driverId = resultSet.getObject(1, Long.class);
                    driver.setId(driverId);
                    carDriverStmt.setLong(1, car.getId());
                    carDriverStmt.setLong(2, driverId);
                    carDriverStmt.executeUpdate();
                }
            }
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet.getLong("manufacturers_id"),
                resultSet.getString("name"), resultSet.getString("country"));
        return new Car(resultSet.getLong("id"),
                resultSet.getString("model"),
                manufacturer,
                null);
    }

    private List<Driver> geListDriver(Connection connection, Long id) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement driversStatement
                     = connection
                .prepareStatement(SELECT_DRIVERS_QUERY_GET_LIST_DRIVERS)) {
            driversStatement.setLong(1, id);
            ResultSet driversResultSet = driversStatement.executeQuery();
            while (driversResultSet.next()) {
                drivers.add(getDriver(driversResultSet));
            }
        }
        return drivers;
    }

    private List<Car> getListCars(ResultSet resultSet) throws SQLException {
        List<Car> cars = new ArrayList<>();
        while (resultSet.next()) {
            Long carId = resultSet.getLong("id");
            String model = resultSet.getString("model");
            Long manufacturerId = resultSet
                    .getObject("manufacturer_id", Long.class);
            String manufacturerName
                    = resultSet.getString("manufacturer_name");
            Manufacturer manufacturer = new Manufacturer(manufacturerId,
                    manufacturerName, null);
            List<Driver> drivers = getDriversForCar(carId);
            Car car = new Car(carId, model, manufacturer, drivers);
            cars.add(car);
        }
        return cars;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }
}
