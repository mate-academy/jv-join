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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name, country FROM cars c JOIN manufacturers m ON c.manufacturer_id "
                + "= m.id WHERE c.is_deleted = FALSE AND c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithoutDrivers(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.setDrivers(getDriversForCar(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, model, manufacturer_id, "
                + "name, country FROM cars c JOIN manufacturers m ON c.manufacturer_id "
                + "= m.id WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithoutDrivers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from db", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car "
                    + car + ". ", e);
        }
        deleteDriversForCar(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id as car_id, model, manufacturer_id, name, country "
                + "FROM cars_drivers cd "
                + "JOIN cars c ON cd.car_id = c.id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithoutDrivers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from db", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers "
                    + car.getDrivers() + ". ", e);
        }
    }

    private Car getCarWithoutDrivers(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet.getObject("manufacturer_id", 
                Long.class), resultSet.getString("name"), 
                resultSet.getString("country"));
        Car car = new Car(resultSet.getObject("car_id",Long.class), 
                resultSet.getString("model"), manufacturer, null);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT cd.driver_id, name, license_number "
            + "FROM cars_drivers cd "
            + "JOIN drivers d ON cd.driver_id = d.id "
            + "WHERE cd.car_id = ?";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car id " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("driver_id", Long.class), 
                resultSet.getString("name"), 
                resultSet.getString("license_number"));
    }

    private void deleteDriversForCar(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete entries from cars_drivers "
                    + "by car id " + carId + ". ", e);
        }
    }
}
