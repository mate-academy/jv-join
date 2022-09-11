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
        String insertQuery = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectQuery = "SELECT c.id, model, manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
                car.setManufacturer(parseManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(car));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllQuery = "SELECT c.id, "
                + "model, manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCar(resultSet);
                car.setManufacturer(parseManufacturer(resultSet));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't get all cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getAllDriversForCar(car)));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car: " + car, e);
        }
        deleteCarsWithDrivers(car.getId());
        insertCarsWithDrivers(car);
        return car;
    }

    private void insertCarsWithDrivers(Car car) {
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers into car " + car, e);
        }
    }

    private int deleteCarsWithDrivers(Long id) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update drivers for car by id " + id, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.id as car_id, model, manufacturer_id, name, "
                + "country FROM cars_drivers cd JOIN cars c on c.id = cd.car_id "
                + "JOIN manufacturers m on m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = false AND cd.driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement
                        = connection.prepareStatement(getAllByDriverQuery)) {
            getAllStatement.setLong(1, driverId);
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCar(resultSet);
                car.setManufacturer(parseManufacturer(resultSet));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver_id " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getAllDriversForCar(car)));
        return cars;
    }

    private List<Driver> getAllDriversForCar(Car car) {
        String getAllDriversByCarQuery = "SELECT d.id, name, license_number, is_deleted "
                + "FROM cars_drivers cd JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = FALSE AND cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getAllDriversByCarQuery)) {
            getDriversStatement.setLong(1, car.getId());
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't find drivers in DB for car by id "
                  + car.getId(), e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("d.id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("c.id", Long.class));
        car.setModel(resultSet.getString("model"));
        return car;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car: " + car, e);
        }
    }
}
