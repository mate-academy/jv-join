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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
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
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car "
                    + car + ". ", throwable);
        }
        insertDriversToDB(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model, "
                        + "m.id as manufacturer_id, m.name, m.country "
                        + "FROM cars c "
                        + "JOIN manufacturers m "
                        + "ON c.manufacturer_id = m.id "
                        + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id "
                    + id + ". ", throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversFromDB(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model, "
                + "m.name, m.id as manufacturer_id, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from DB. ", throwable);
        }
        setDriversToCars(cars);
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * "
                + "FROM cars_drivers cd "
                + "JOIN cars c "
                + "ON cd.car_id = c.id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from DB by driver id "
                    + driverId + ". ", throwable);
        }
        setDriversToCars(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars_driversDB.", throwable);
        }
        deleteCarFromDB(car);
        insertDriversToDB(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id "
                    + id + ". ", throwable);
        }
    }

    private void insertDriversToDB(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert driver to car by id "
                    + car.getId() + ". ", throwable);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversFromDB(Long id) {
        String query = "SELECT d.id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get driver by id "
                    + id + ". ", throwable);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void setDriversToCars(List<Car> cars) {
        for (Car car : cars) {
            car.setDrivers(getDriversFromDB(car.getId()));
        }
    }

    private boolean deleteCarFromDB(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete "
                    + car + " from cars_drivers table. ", throwable);
        }
    }
}
