package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query =
                "INSERT INTO cars (number_plate, year, manufacturer_id) VALUES " + "(?, ?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getNumberPlate());
            saveCarStatement.setInt(2, car.getYear());
            saveCarStatement.setLong(3, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }
    
    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.number_plate, c.year, m.name, m.country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id WHERE c.id = ? AND c.deleted = "
                + "FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }
    
    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.number_plate, c.year, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id AND c.deleted = "
                + "FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.",
                    throwable);
        }
    }
    
    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET year = ?, number_plate = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setInt(1, car.getYear());
            updateCarStatement.setString(2, car.getNumberPlate());
            updateCarStatement.setLong(3, car.getManufacturer().getId());
            updateCarStatement.setLong(4, car.getId());
            if (updateCarStatement.executeUpdate() == 0) {
                throw new RuntimeException(
                        "There is no such car to update. Car id: " + car.getId());
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update " + car + " in driversDB.",
                    throwable);
        }
        removeDrivers(car);
        insertDrivers(car);
        return car;
    }
    
    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }
    
    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT car_id as id, c.number_plate, c.year, m.country, m.name "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id  "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.",
                    throwable);
        }
    }
    
    private void removeDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getId());
        } catch (SQLException throwable) {
            throw new DataProcessingException(
                    "Couldn't delete drivers associated with car ID: " + car.getId()
                            + " in driversDB.", throwable);
        }
    }
    
    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String numberPlate = resultSet.getString("number_plate");
        Integer year = resultSet.getObject("year", Integer.class);
        Car car = new Car(year, numberPlate, getDriverList(carId), getManufacturer(resultSet));
        car.setId(carId);
        return car;
    }
    
    private List<Driver> getDriverList(Long carId) {
        String query = "SELECT cd.car_id as id, name, license_number FROM drivers d JOIN "
                + "cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.deleted = "
                + "FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverListStatement = connection.prepareStatement(query)) {
            getDriverListStatement.setLong(1, carId);
            ResultSet resultSet = getDriverListStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriver(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get a list of drivers from driversDB with " + "specified car ID: "
                            + carId, e);
        }
    }
    
    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
    
    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(newId);
        return manufacturer;
    }
    
    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection.prepareStatement(query)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers to car ID: " + car.getId(), e);
        }
    }
}
