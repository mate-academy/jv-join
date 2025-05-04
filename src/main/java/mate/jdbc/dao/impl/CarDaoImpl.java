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
        String insertCarRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(insertCarRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setObject(1,car.getManufacturer().getId());
            createCarStatement.setString(2,car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not insert car to DB. " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT c.id AS car_id, model,"
                + "manufacturer_id, m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1,id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parserCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get Car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id AS car_id, model, "
                + "manufacturer_id, m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = parserCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get all cars from DB." + e);
        }
        for (Car currentCar : cars) {
            currentCar.setDrivers(getDriverForCar(currentCar.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET manufacturer_id = ?,"
                + " model = ? WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2,car.getModel());
            updateCarStatement.setLong(3,car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Could not update car from DB. " + car,e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Could not delete car from DB by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id AS car_id, model,"
                + "manufacturer_id, m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> carsByDriver = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement =
                        connection.prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsByDriverStatement.setLong(1,driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                car = parserCarWithManufacturerFromResultSet(resultSet);
                carsByDriver.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get all cars by driver id " + driverId, e);
        }

        for (Car currentCar : carsByDriver) {
            currentCar.setDrivers(getDriverForCar(car.getId()));
        }
        return carsByDriver;
    }

    private void deleteDrivers(Car car) {
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement =
                        connection.prepareStatement(deleteDriversRequest)) {
            deleteDriversStatement.setLong(1,car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Could not delete drivers by car " + car, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(1,car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2,driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not insert drivers to car " + car, e);
        }
    }

    private Car parserCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName("manufacturer_name");
        manufacturer.setCountry("manufacturer_country");
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriverForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, licenseNumber "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1,carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get drivers for car by id " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }

    private Car containsCarFromResultSet(ResultSet resultSet) throws java.sql.SQLException {
        Car car = null;
        while (resultSet.next()) {
            car = parserCarWithManufacturerFromResultSet(resultSet);
            car.setDrivers(getDriverForCar(car.getId()));
        }
        return car;
    }
}
