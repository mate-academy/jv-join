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
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDriverToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Optional<Car> car = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = Optional.of(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.ifPresent(value -> value.setDrivers(getDriversList(id)));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT car.id, model, "
                + "manufacturer_id, manufacturer.name, manufacturer.country FROM cars car "
                + "JOIN manufacturers manufacturer ON car.manufacturer_id = manufacturer.id "
                + "WHERE car.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        deleteCarsDriversRelationsForCar(car.getId());
        insertDriverToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE car SET is_delete = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String query = "SELECT cars.id, model, manufacturers.id, name, country "
                + "FROM cars JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars_drivers.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars with id:" + id, e);
        }
    }

    private void insertDriverToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers into car " + car, e);
        }
    }

    private void deleteCarsDriversRelationsForCar(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars drivers relations by car id "
                    + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer("name", "country");
        manufacturer.setId(resultSet.getObject("manufacturers.id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(driverId, name, licenseNumber);
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT driver.* FROM drivers driver "
                + "JOIN cars_drivers car_driver ON driver.id = car_driver.driver_id "
                + "WHERE car_driver.car_id = ? AND driver.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car id " + id, e);
        }
    }

    private List<Driver> getDriversList(Long carId) {
        String carDriver = "SELECT id, name, license_number FROM drivers"
                + " JOIN cars_drivers ON drivers.id = cars_drivers.driver_id"
                + " WHERE is_deleted = FALSE AND cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(carDriver)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(parseCars(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers with id:" + carId, e);
        }
    }

    private Driver parseCars(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver("name", "license_number");
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }
}
