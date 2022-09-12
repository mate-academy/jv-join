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
        String createQuery = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(createQuery,
                         Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car. Car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT name, model, manufacturers_id, country, cars.id "
                + "FROM cars join manufacturers on manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithResultSet(resultSet);
                car.setDrivers(getDriversById(id));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getQuery = "SELECT name, model, manufacturers_id, country, cars.id "
                + "FROM cars JOIN manufacturers ON manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(getQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                car = parseCarWithResultSet(resultSet);
                car.setDrivers(getDriversById(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all car", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturers_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement =
                         connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update " + car, e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, cars.model, manufacturers.id, "
                + "manufacturers.name, manufacturers.country, manufacturers_id "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturers_id = manufacturers.id "
                + "JOIN cars_drivers ON cars.id = cars_drivers.cars_id "
                + "WHERE cars_drivers.drivers_id = ? AND cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of cars for driver. Id: " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversById(car.getId()));
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (cars_id, drivers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement(insertDriversQuery)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, e);
        }
    }

    private List<Driver> getDriversById(Long carId) {
        String query = "SELECT id, name, license_number FROM drivers"
                + " JOIN cars_drivers ON drivers.id = cars_drivers.drivers_id "
                + "WHERE cars_drivers.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get Drivers by car id", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private Car parseCarWithResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private void deleteDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteDriverStatement =
                         connection.prepareStatement(deleteDriversQuery)) {
            deleteDriverStatement.setLong(1, car.getId());
            deleteDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete delete driver from car " + car, e);
        }
    }
}
