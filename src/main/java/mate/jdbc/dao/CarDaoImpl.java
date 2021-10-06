package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id AS car_id , model, mf.id, mf.name, mf.country "
                + "FROM CARS c JOIN manufacturers mf "
                + "ON c.manufacturer_id = mf.id WHERE c.id = ? AND c.is_deleted =false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            getCarStatement.executeQuery();
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't find car in DB. ", throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id AS car_id, model, mf.id, mf.name, mf.country "
                + "FROM cars c JOIN manufacturers mf "
                + "ON c.manufacturer_id = mf.id WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsStatement = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement softDeleteCarStatement = connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1, id);
            int numberOfRowsDeleted = softDeleteCarStatement.executeUpdate();
            return numberOfRowsDeleted != 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car from DB. ", throwable);
        }
    }

        @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriverToCarStatement = connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert driver. ", throwable);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllDriversStatement = connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get driver. ", throwable);
        }
    }

    private Car parseCarFromResultSet (ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriverFromResultSet (ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
