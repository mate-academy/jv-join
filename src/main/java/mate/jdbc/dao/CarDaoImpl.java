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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setLong(1, car.getManufacturer().getId());
            insertStatement.setString(2, car.getModel());
            insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT * FROM cars JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id WHERE cars.id = ? and cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car with this id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getListDriversInCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id WHERE cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                car.setDrivers(getListDriversInCar(car.getId()));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from db", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getManufacturer().getId());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update this car " + car + " in db", e);
        }
        deleteDriversInCar(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, carId);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id: " + carId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject(1, Long.class));
        return car;
    }

    private List<Driver> getListDriversInCar(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers JOIN drivers_cars dc ON drivers.id = dc.driver_id "
                + "WHERE is_deleted = false AND dc.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getListDriversStatement = connection.prepareStatement(query)) {
            getListDriversStatement.setLong(1, id);
            ResultSet resultSet = getListDriversStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("driver_id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list drivers with car id:" + id, e);
        }
    }

    private void deleteDriversInCar(Car car) {
        String query = "DELETE FROM drivers_cars WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers in the car: " + car, e);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO drivers_cars (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection.prepareStatement(query)) {
            insertDriversStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(1, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers for this car into DB:"
                    + car, e);
        }
    }
}
