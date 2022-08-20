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
    @Inject
    ManufacturerDao manufacturerDao;
    @Inject
    DriverDao driverDao;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
                insertCarDrivers(car);
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    private void insertCarDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver: car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers." , e);
        }

    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars WHERE id = ? AND is_deleted = FALSE";
        Optional<Car> carOptional = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                carOptional = Optional.of(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers." , e);
        }
        return carOptional;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE is_deleted=FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                 cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers." , e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id=?, cars.model=? WHERE cars.id=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
            deleteRelations(car.getId());
            insertCarDrivers(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers.", e);
        }
        return car;
    }

    private void deleteRelations(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted=TRUE WHERE id=?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers." , e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, manufacturer_id, model FROM cars " +
                "INNER JOIN cars_drivers ON cars.id=cars_drivers.car_id " +
                "WHERE driver_id=?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add car drivers.", e);
        }
        return cars;
    }


    private Car createCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Optional<Manufacturer> manufacturer = manufacturerDao
                .get(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer.orElseThrow(() -> new RuntimeException("Cant get manufacturer to car.")));
        car.setDrivers(getDrivers(car.getId()));
        return car;
    }

    private List<Driver> getDrivers(Long carId) {
        String query = "SELECT * FROM cars_drivers WHERE car_id=?";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Optional<Driver> driver = driverDao.get(resultSet.getObject(2, Long.class));
                drivers.add(driver.orElseThrow(() -> new RuntimeException("Cant get driver for List.")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get drivers for car", e);
        }
        return drivers;
    }
}
