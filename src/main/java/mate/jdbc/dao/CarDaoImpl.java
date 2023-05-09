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
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
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
            throw new DataProcessingException("Couldn't insert drivers for "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, "
                + "name AS name_manufacturer, country AS country_manufacturer "
                + "FROM cars INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                car = parseCarWithCarsDriversFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getListDriversFromCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, "
                + "name AS name_manufacturer, country AS country_manufacturer "
                + "FROM cars INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                cars.add(parseCarWithCarsDriversFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from db ", e);
        }
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).setDrivers(getListDriversFromCar(cars.get(i).getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteCarDrivers(car);
        insertDrivers(car);
        return car;
    }

    private void deleteCarDrivers(Car car) {
        String query = "DELETED cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                           = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers for "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, drivers_id "
                + "FROM cars INNER JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE driver_id = 2 AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            if (resultSet.next()) {
                cars.add(get(resultSet.getObject("car_id", Long.class)).get());
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list cars by id " + driverId, e);
        }
    }

    private Car parseCarWithCarsDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name_manufacturer"));
        manufacturer.setCountry(resultSet.getString("country_manufacturer"));
        return car;
    }

    public List<Driver> getListDriversFromCar(Long carsId) {
        String query = "SELECT drivers.id AS driver_id, name, license_number "
                + "FROM drivers INNER JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE car_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carsId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            if (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + carsId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }
}
