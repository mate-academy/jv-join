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
        String query = "INSERT INTO cars (model ,manufacturer_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                         = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        addRelationCarDriver(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.model, manufacturers.id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE "
                + "AND manufacturers.is_deleted = FALSE AND cars.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id,e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, manufacturers.id, manufacturers.name,"
                + " manufacturers.country FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
        for (Car car:cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id  = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update new data on " + car, e);
        }
        deleteRelationCarDriver(car);
        addRelationCarDriver(car);
        return car;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT driver_id, cars.id, cars.model, "
                + "cars.manufacturer_id, manufacturers.id,"
                + " manufacturers.name, manufacturers.country FROM cars_drivers "
                + "JOIN cars ON car_id = cars.id JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car by id driver "
                    + driverId, e);
        }
        for (Car car:cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id,e);
        }
    }

    private void addRelationCarDriver(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't connect connection between driver and car", e);
        }
    }

    private void deleteRelationCarDriver(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete connection between driver and car", e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        car.setId(resultSet.getObject("cars.id",Long.class));
        car.setModel(resultSet.getString("cars.model"));
        manufacturer.setId(resultSet.getObject("manufacturers.id",Long.class));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDrivers(Long carId) {
        String query = "SELECT car_id, driver_id, drivers.name, drivers.license_number "
                + "FROM cars_drivers JOIN drivers ON driver_id = drivers.id "
                + "WHERE drivers.is_deleted = false AND car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by id "
                    + carId, e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("car_id",Long.class));
        driver.setName(resultSet.getString("drivers.name"));
        driver.setLicenseNumber(resultSet.getString("drivers.license_number"));
        return driver;
    }
}
