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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement insertStatement = connection.prepareStatement(
                            query, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setLong(1, car.getManufacturer().getId());
            insertStatement.setString(2, car.getModel());
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car " + car, e);
        }
        addDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE AND c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setLong(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement selectStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setLong(1, car.getManufacturer().getId());
            updateStatement.setString(2, car.getModel());
            updateStatement.setLong(3, car.getId());
            if (updateStatement.executeUpdate() < 1) {
                throw new DataProcessingException("The were not any updates for car " + car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteDrivers(car.getId());
        addDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.manufacturer_id, c.model "
                + "FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setLong(1, driverId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars of driver by id " + driverId, e);
        }
        return cars;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject(1, Long.class);
        Long manufacturerId = resultSet.getObject(2, Long.class);
        String model = resultSet.getString(3);
        String manufacturerName = resultSet.getString(4);
        String manufacturerCountry = resultSet.getString(5);
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        Car car = new Car(carId, model, manufacturer);
        car.setDrivers(getAllDriversByCar(carId));
        return car;
    }

    private List<Driver> getAllDriversByCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setLong(1, carId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get all drivers by car id " + carId, e);
        }
        return drivers;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(1, Long.class);
        String name = resultSet.getString(2);
        String licenseNumber = resultSet.getString(3);
        return new Driver(id, name, licenseNumber);
    }

    private void addDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement insertStatement = connection.prepareStatement(query)) {
            insertStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(1, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into cars_driver table", e);
        }
    }

    private void deleteDrivers(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, carId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from cars_drivers table", e);
        }
    }
}
