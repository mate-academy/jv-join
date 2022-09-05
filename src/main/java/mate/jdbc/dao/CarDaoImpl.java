package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static final String TABLE = "cars";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";
    private static final String COLUMN_IS_DELETED = "is_deleted";
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        String queryCreateCar = "INSERT INTO " + TABLE + " ("
                + COLUMN_MODEL + ", " + COLUMN_MANUFACTURER_ID + ") VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(queryCreateCar,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet createCarResultSet = createCarStatement.getGeneratedKeys();
            if (createCarResultSet.next()) {
                car.setId(createCarResultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        assignDriversToTheCar(car);
        return car;
    }

    @Override
    public Car update(Car car) {
        String queryUpdateCar = "UPDATE " + TABLE + " SET " + COLUMN_MODEL + " = ?, "
                + COLUMN_MANUFACTURER_ID + " = ? WHERE id = ? AND "
                + COLUMN_IS_DELETED + " = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(queryUpdateCar)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update info for the car " + car, e);
        }
        updateDriversFor(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String queryDeleteCar = "UPDATE " + TABLE + " SET "
                + COLUMN_IS_DELETED + " = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(queryDeleteCar)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete the Car with id = " + id, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String queryGetCar = "SELECT * FROM " + TABLE + " WHERE id = ? AND "
                + COLUMN_IS_DELETED + " = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(queryGetCar)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            Car car = null;
            if (carResultSet.next()) {
                car = getCar(carResultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get the car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String queryGetAllCars = "SELECT * FROM " + TABLE + " WHERE "
                + COLUMN_IS_DELETED + " = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement allCarsStatement = connection.prepareStatement(queryGetAllCars)) {
            ResultSet carsResultSet = allCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                cars.add(getCar(carsResultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return getAll().stream()
                .filter(car -> isContainDriver(driverId, car))
                .collect(Collectors.toList());
    }

    private static boolean isContainDriver(Long driverId, Car car) {
        for (Driver driver: car.getDrivers()) {
            if (Objects.equals(driver.getId(), driverId)) {
                return true;
            }
        }
        return false;
    }

    private void assignDriversToTheCar(Car car) {
        String query = "INSERT INTO cars_drivers (id_car, id_driver) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            for (Driver driver: car.getDrivers()) {
                try (PreparedStatement createCarsDriversStatement =
                             connection.prepareStatement(query)) {
                    createCarsDriversStatement.setLong(1, car.getId());
                    createCarsDriversStatement.setLong(2, driver.getId());
                    createCarsDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't assign drivers for the car " + car, e);
        }
    }

    private void updateDriversFor(Car car) {
        String queryRemovePrevDrivers = "DELETE FROM cars_drivers WHERE id_car = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversStatement =
                        connection.prepareStatement(queryRemovePrevDrivers)) {
            removeDriversStatement.setLong(1, car.getId());
            removeDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update drivers for the car " + car, e);
        }
        assignDriversToTheCar(car);
    }

    private Car getCar(ResultSet carResultSet) throws SQLException {
        Long id = carResultSet.getObject("id", Long.class);
        String model = carResultSet.getString(COLUMN_MODEL);
        Manufacturer manufacturer = manufacturerDao
                .get(carResultSet.getObject(COLUMN_MANUFACTURER_ID, Long.class))
                .orElseThrow(SQLException::new);
        List<Driver> drivers = getAllDriversAssignedCar(id);
        return new Car(id, model, manufacturer, drivers);
    }

    private List<Driver> getAllDriversAssignedCar(Long id) {
        String queryGetAllDriversAssignedCar =
                "SELECT id_driver FROM cars_drivers WHERE id_car = ?";
        List<Driver> result = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarDriversStatement =
                        connection.prepareStatement(queryGetAllDriversAssignedCar)) {
            getAllCarDriversStatement.setLong(1, id);
            ResultSet driversIdResultSet = getAllCarDriversStatement.executeQuery();
            while (driversIdResultSet.next()) {
                Driver driver = driverDao
                        .get(driversIdResultSet.getLong(1))
                        .orElseThrow(SQLException::new);
                result.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for the car by id " + id, e);
        }
        return result;
    }
}
