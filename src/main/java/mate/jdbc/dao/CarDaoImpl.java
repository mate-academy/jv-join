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
            statement.setLong(2, car.getManufacturerId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            addDriversToCar(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't creat new car with next data: model - "
                    + car.getModel() + ", manufacturer_id - "
                    + car.getManufacturerId(), e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT cars.id as carId, cars.model, "
                + "manufacturers.id as manufacturerId, manufacturers.name as manufacturer, "
                + "manufacturers.country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = false";
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = collectCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with id - " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id as carId, cars.model, "
                + "manufacturers.id as manufacturerId, manufacturers.name as manufacturer, "
                + "manufacturers.country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                Car car = collectCar(resultSet);
                car.setDrivers(getDriversFromCar(car));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT drivers.id, drivers.name, drivers.license_number, "
                + "manufacturers.id as manufacturerId, "
                + "manufacturers.name as manufacturer, manufacturers.country, "
                + "cars.id AS carId, cars.model "
                + "FROM  cars_drivers "
                + "INNER JOIN cars on cars_drivers.car_id = cars.id "
                + "INNER JOIN manufacturers on cars.manufacturer_id = manufacturers.id "
                + "INNER JOIN drivers on cars_drivers.driver_id = drivers.id "
                + "WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = collectCar(resultSet);
                car.getDrivers().add(collectDriver(resultSet));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars of driver with driverId - "
                    + driverId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE cars.id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturerId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with next data: "
                    + "model" + car.getModel()
                    + "manufacturer_id" + car.getManufacturerId(), e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true "
                + "WHERE cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id - " + id, e);
        }
    }

    private Car collectCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("carId"));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturerId"));
        manufacturer.setName(resultSet.getString("manufacturer"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturerId(manufacturer.getId());
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver collectDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getLong("id"));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private List<Driver> getDriversFromCar(Car car) {
        String query = "SELECT drivers.id as driverId, drivers.name, drivers.license_number, "
                + "cars.id, driver_id, car_id FROM cars_drivers "
                + "INNER JOIN drivers on cars_drivers.driver_id = drivers.id "
                + "INNER JOIN cars on cars_drivers.car_id = cars.id "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(collectDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of drivers of car - "
                    + car.getModel(), e);
        }
    }

    private void addDriversToCar(Car car) {
        if (car.getDrivers().isEmpty()) {
            return;
        }
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to car - " + car.getModel(), e);
        }
    }
}
