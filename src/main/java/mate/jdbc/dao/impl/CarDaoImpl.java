package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dao
public class CarDaoImpl implements CarDao {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO taxi_service.cars (model, manufacturers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement =
                     connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not insert data to DB", e);
        }
        insertDrivers(car);
        return car;
    }


    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT cars.id, model, manufacturers_id, name, country FROM cars "
                + "JOIN manufacturers ON cars.manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement =
                     connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get data by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> allCars = new ArrayList<>();
        String selectRequest = "SELECT cars.id, model, manufacturers_id, name, country "
                + "FROM cars JOIN manufacturers ON cars.manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement =
                     connection.prepareStatement(selectRequest)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                car.setDrivers(getDriversForCar((resultSet.getObject("cars.id", Long.class))));
                allCars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get all data from DB", e);
        }
        return allCars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturers_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement
                     = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            deleteFromCarsAndDrivers(car.getId());
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement softDeletedCarStatement
                     = connection.prepareStatement(query)) {
            softDeletedCarStatement.setLong(1, id);
            int numberOfDeletedRows = softDeletedCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT id, model , manufacturers_id "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriverStatement = connection.prepareStatement(query)) {
            getDriverStatement.setLong(1, driverId);
            ResultSet generatedKeys = getDriverStatement.executeQuery();
            while (generatedKeys.next()) {
                Car car = get(generatedKeys.getObject("id", Long.class));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get cars by driver id: " + driverId, e);
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("cars.id", Long.class));
        return car;
    }

    private void deleteFromCarsAndDrivers(Long id) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteByIDStatement
                     = connection.prepareStatement(deleteRequest)) {
            deleteByIDStatement.setLong(1, id);
            deleteByIDStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete data from cars_drivers by id: " + id, e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number " +
                "FROM drivers a " +
                "JOIN cars_drivers ba " +
                "ON a.id = ba.driver_id " +
                "WHERE ba.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllDrivers = connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDrivers.setLong(1, id);
            ResultSet resultSet = getAllDrivers.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get drivers for id:" + id, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id,driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriverToCarStatement =
                     connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                if (driver.getId() == null) { //add driver to DB if don't exist
                    DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
                    driverService.create(driver);
                }
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not insert drivers to car: " + car, e);
        }
    }
}
