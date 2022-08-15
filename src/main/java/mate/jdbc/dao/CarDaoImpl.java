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
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(createCarQuery,
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
        String getCarByIdQuery = "SELECT id, model, manufacturer_id "
                + "FROM cars WHERE id = ? AND is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getCarByIdQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversForCarQuery = "SELECT cars_drivers.driver_id "
                + "FROM cars JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars.id = ?";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getDriversForCarQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            DriverDaoImpl driverDao = new DriverDaoImpl();
            while (resultSet.next()) {
                Optional<Driver> driver = driverDao.get(resultSet.getLong("driver_id"));
                driver.ifPresent(drivers::add);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars drivers by id " + id, e);
        }
        return drivers;
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT * FROM cars WHERE is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                Car car = getCarWithManufacturer(resultSet);
                car.setDrivers(getDriversForCar(id));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(updateCarQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars table", e);
        }
        updateCarsDriversTable(car);
        return car;
    }

    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverQuery = "SELECT cars_drivers.car_id "
                + "FROM cars_drivers WHERE cars_drivers.driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(getAllCarsByDriverQuery)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            CarDaoImpl carDao = new CarDaoImpl();
            while (resultSet.next()) {
                Optional<Car> car = carDao.get(resultSet.getLong("car_id"));
                car.ifPresent(cars::add);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id " + driverId, e);
        }
        return cars;
    }

    private void updateCarsDriversTable(Car car) {
        deleteDrivers(car);
        insertDrivers(car);
    }

    private void deleteDrivers(Car car) {
        String dropDriversByCarIdQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(dropDriversByCarIdQuery)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't drop drivers of car "
                    + car + " in cars table cars_drivers", e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriverQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertDriverQuery)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car. ", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteCarQuery)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id: " + id, e);
        }
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        ManufacturerDaoImpl manufacturerDao = new ManufacturerDaoImpl();
        Long manufacturerId = resultSet.getLong("manufacturer_id");
        Manufacturer manufacturer = manufacturerDao.get(manufacturerId)
                .orElseThrow(() -> new RuntimeException("Can't find manufacturer id: "
                        + manufacturerId));
        Car car = new Car(id, model, manufacturer);
        return car;
    }
}
