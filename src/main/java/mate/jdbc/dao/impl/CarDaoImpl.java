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
        String query = "INSERT INTO `cars` (`model`, `manufacturer_id`) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCar = connection.prepareStatement(query,
                                       Statement.RETURN_GENERATED_KEYS)) {
            createCar.setString(1, car.getModel());
            createCar.setLong(2, car.getManufacturer().getId());
            createCar.executeUpdate();
            ResultSet resultSet = createCar.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCar = connection.prepareStatement(query)) {
            getCar.setLong(1, id);
            ResultSet resultSet = getCar.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id + ". ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversFromCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT DISTINCT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAll = connection.prepareStatement(query)) {
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from DB. ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversFromCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCar = connection.prepareStatement(query)) {
            updateCar.setString(1, car.getModel());
            updateCar.setLong(2, car.getManufacturer().getId());
            updateCar.setLong(3, car.getId());
            updateCar.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + ". ", e);
        }
        removeDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCar = connection.prepareStatement(query)) {
            deleteCar.setLong(1, id);
            return deleteCar.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id = " + id + ". ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT DISTINCT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, name, country "
                + "FROM cars c "
                + "JOIN cars_drivers cd on c.id = cd.car_id "
                + "JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE "
                + "ORDER BY c.id, m.id;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriver = connection.prepareStatement(query)) {
            getAllByDriver.setLong(1, driverId);
            ResultSet resultSet = getAllByDriver.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driverId = "
                    + driverId + ". ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversFromCar(car.getId()));
        }
        return cars;
    }

    private void removeDrivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDrivers = connection.prepareStatement(query)) {
            removeDrivers.setLong(1, id);
            removeDrivers.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers for car by id = "
                    + id + ". ", e);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCar = connection.prepareStatement(query)) {
            insertDriversToCar.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversToCar.setLong(2, driver.getId());
                insertDriversToCar.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add [" + car.getDrivers()
                    + "] from " + car + ". ", e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getObject("car_id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(
                        resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")
                )
        );
    }

    private List<Driver> getDriversFromCar(Long id) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd on d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDrivers = connection.prepareStatement(query)) {
            getDrivers.setLong(1, id);
            ResultSet resultSet = getDrivers.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id + ". ", e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number")
        );
    }
}
