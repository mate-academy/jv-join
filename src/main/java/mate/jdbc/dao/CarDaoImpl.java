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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
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
            throw new DataProcessingException("Couldn't create car: "
                    + car + ". ", e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.manufacturer_id, cars.model, manufacturers.name, "
                + "manufacturers.country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.manufacturer_id, cars.model, "
                + "manufacturers.id, manufacturers.name, manufacturers.country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id AND cars.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET cars.model = ?,"
                + "cars.manufacturer_id = ? WHERE cars.id = ? AND cars.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        removeDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET cars.is_deleted = true "
                + "WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return (deleteStatement.executeUpdate() > 0);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query =
                "SELECT * FROM cars INNER JOIN manufacturers "
                        + "ON cars.manufacturer_id = manufacturers.id INNER "
                        + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                        + "WHERE cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) {
        Car car = new Car();
        try {
            car.setId(resultSet.getObject("cars.id", Long.class));
            car.setModel(resultSet.getString("cars.model"));
            Manufacturer manufacturer = new Manufacturer(
                    resultSet.getObject("manufacturer_id", Long.class),
                    resultSet.getString("manufacturers.name"),
                    resultSet.getString("manufacturers.country"));
            car.setManufacturer(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car from DB.");
        }
        return car;
    }

    private List<Driver> getDriversForCar(Car car) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers INNER JOIN cars_drivers "
                        + "ON cars_drivers.driver_id = drivers.id WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversForCarStatement =
                        connection.prepareStatement(query)) {
            getAllDriversForCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers of car " + car, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) {
        Driver driver = new Driver();
        try {
            driver.setId(resultSet.getObject("driver_id", Long.class));
            driver.setName(resultSet.getString("name"));
            driver.setLicenseNumber(resultSet.getString("license_number"));
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver from DB.");
        }
        return driver;
    }

    private void removeDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversFromCarStatement
                        = connection.prepareStatement(query)) {
            removeDriversFromCarStatement.setLong(1, car.getId());
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers from car " + car, e);
        }
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection.prepareStatement(query)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car " + car, e);
        }
    }
}
