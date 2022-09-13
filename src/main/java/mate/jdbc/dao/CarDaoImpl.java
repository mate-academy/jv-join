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
        String createQuery = "INSERT INTO taxi_service_db.cars (model,manufacturer_id)"
                + " VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            addDriversToCarsInDb(car);
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t create Car: " + car + " to DB", throwables);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ?;";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get car by id:" + id, throwables);
        }
        car.setDrivers(getDriversForCar(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all Cars from DB", throwables);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().getId());
            preparedStatement.setObject(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t update Car:" + car, throwables);
        }
        removeDriversFromCar(car);
        addDriversToCarsInDb(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String queryFroUpdate = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryFroUpdate)) {
            preparedStatement.setObject(1, id);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t delete Car by id: " + id, throwables);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars "
                 + "JOIN cars_drivers ON cars_drivers.cars_id = cars.id "
                 + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                 + "WHERE drivers_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all "
                  + "Cars by drivers id: " + driverId, throwables);
        }

        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void addDriversToCarsInDb(Car car) {
        String query = "INSERT INTO cars_drivers (cars_id, drivers_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setObject(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t add drivers to cars in DB", throwables);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.drivers_id "
                + "WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = getDriverFromResultSet(resultSet);
                drivers.add(driver);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get "
                  + "Drivers from DB by Cars id:" + id, throwables);
        }

        return drivers;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(resultSet.getString(3));
        manufacturer.setName(resultSet.getString(2));
        manufacturer.setId(resultSet.getObject(1, Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private void removeDriversFromCar(Car car) {
        String queryForDelete = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryForDelete)) {
            preparedStatement.setObject(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t remove drivers from car:" + car, throwables);
        }
    }
}
