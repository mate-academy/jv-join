package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT id, model, manufacturer_id, driver_id "
                + "FROM cars c JOIN cars_drivers cd "
                + "ON c.id = cd.car_id where is_deleted = false AND c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        Long carId = car.getId();
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, carId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        List<Driver> driversForCar = car.getDrivers();
        List<Driver> currentDrivers = getDriversForCar(carId);
        if (driversForCar.size() > currentDrivers.size()) {
            for (Driver driver : driversForCar) {
                if (!currentDrivers.contains(driver)) {
                    insertDriverToCar(carId, driver.getId());
                }
            }
        } else if (driversForCar.size() < currentDrivers.size()) {
            for (Driver driver : currentDrivers) {
                if (!driversForCar.contains(driver)) {
                    deleteDriverFromCar(carId, driver.getId());
                }
            }
        }
        return car;
    }

    private void deleteDriverFromCar(Long carId, Long driverId) {
        String query = "DELETE FROM cars_drivers WHERE driver_id = ? AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            statement.setLong(2, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers for car by id "
                    + carId + " in carsDB.", e);
        }
    }

    private void insertDriverToCar(Long carId, Long driverId) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            statement.setLong(2, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers for car by id "
                    + carId + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "JOIN cars c ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE AND d.id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "for driver by id " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        ManufacturerServiceImpl manufacturerService =
                (ManufacturerServiceImpl) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer =
                manufacturerService.get(resultSet.getObject("manufacturer_id", Long.class));
        DriverServiceImpl driverService =
                (DriverServiceImpl) injector.getInstance(DriverService.class);
        List<Driver> drivers = getDriversForCar(id);
        return new Car(id, model, manufacturer, drivers);
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT * FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.is_deleted = false;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(new DriverDaoImpl().getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers "
                    + "for car by id " + carId, e);
        }
    }
}
