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
        String createCarRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car + " ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id,"
                + " m.name AS manufacturer_name, m.country AS manufacturer_country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id + " ", e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id,"
                + " m.name AS manufacturer_name, m.country AS manufacturer_country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement
                        = connection.prepareStatement(getAllCarRequest)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ", e);
        }
        cars.forEach(car -> car.setDrivers(getAllDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public List<Car> getAllByDriverId(Long driverId) {
        String getCarsByDriverId = "SELECT c.id AS car_id, c.model, c.manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverIdStatement
                        = connection.prepareStatement(getCarsByDriverId)) {
            getCarsByDriverIdStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverIdStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driver id "
                    + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getAllDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car " + car + " ", e);
        }
        deleteDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id + " ", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getAllDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversForCarRequest)) {
            List<Driver> drivers = new ArrayList<>();
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of drivers for car by id "
                    + carId + " ", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement
                        = connection.prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car "
                    + car + " ", e);
        }
    }

    private void deleteDrivers(Long carId) {
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(deleteDriversRequest)) {
            deleteDriversStatement.setLong(1, carId);
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers by car id" + carId + " ", e);
        }
    }
}
