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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (name, manufactur_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement statement = connection.prepareStatement(query,
                               Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getName());
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
        addRelation(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT id, name "
                + "FROM cars "
                + "WHERE id = ? AND is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCar(id));
        }
        return car;
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car c: cars) {
            c.setDrivers(getDriversByCar(c.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET name = ?, manufactur_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setString(1, car.getName());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteRelation(car);
        addRelation(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        Boolean deletion;
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            deletion = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
        deleteRelation(get(id));
        return deletion;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, cars.name "
                + "FROM cars JOIN car_drivers cd ON cars.id = car_drivers.car_id "
                + "WHERE is_deleted = FALSE AND cd.driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car c: cars) {
            c.setDrivers(getDriversByCar(c.getId()));
        }
        return cars;
    }

    public void deleteRelation(Car car) {
        List<Driver> drivers = car.getDrivers();
        String query = "UPDATE car_drivers SET is_deleted = true WHERE car_id = ? ";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete reletion with car id "
                    + car.getId(), e);
        }
    }

    private void addRelation(Car car) {
        List<Driver> drivers = car.getDrivers();
        String query = "INSERT INTO "
                + "car_drivers (driver_id, car_id)"
                + " VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            for (Driver d: drivers) {
                statement.setLong(1, d.getId());
                statement.setLong(2, car.getId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create relation. ", e);
        }
    }

    public List<Driver> getDriversByCar(Long carId) {
        String query = "SELECT * FROM drivers JOIN car_drivers cd "
                     + "ON drivers.id = cd.driver_id "
                     + "WHERE cd.car_id = ? ";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers from carsDB.", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        return new Car(id, name);
    }

}
