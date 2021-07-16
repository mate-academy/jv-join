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
        String query = "INSERT INTO cars (manufacture_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car " + car, throwable);
        }
        insertCarsDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, model, d.id AS driver_id, m.id, "
                + " d.name AS driver_name, m.name, m.country, d.license_number "
                + "FROM cars AS c JOIN cars_drivers AS cd ON cd.car_id = c.id "
                + "JOIN drivers AS d ON cd.driver_id = d.id "
                + "JOIN manufacturers AS m ON c.manufacture_id = m.id "
                + "WHERE c.id = ? AND  c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Car car = null;
            List<Driver> drivers = new ArrayList<>();
            if (resultSet.next()) {
                car = parseManufactureWithCarFromResultSet(resultSet);
                do {
                    drivers.add(parseDriversFromResultSet(resultSet));
                } while (resultSet.next());
                car.setDrivers(drivers);
            }
            return Optional.ofNullable(car);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.model, m.id, m.name, m.country, c.id AS car_id "
                + " FROM cars AS c JOIN manufacturers AS m ON c.manufacture_id = m.id "
                + " WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseManufactureWithCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacture_id = ?, model = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a car " + car, throwable);
        }
        return insertCarsDrivers(car);
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model, m.id, m.country, m.name "
                + " FROM cars AS c JOIN cars_drivers AS cd "
                + " ON cd.car_id = c.id JOIN drivers AS d "
                + " ON cd.driver_id = d.id JOIN manufacturers AS m "
                + " ON c.manufacture_id = m.id "
                + " WHERE d.id = ? AND  c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            if (resultSet.next()) {
                cars.add(parseManufactureWithCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id AS driver_id, d.name AS driver_name, d.license_number "
                + "FROM cars AS c "
                + "JOIN cars_drivers AS cd "
                + "ON cd.car_id = c.id "
                + "JOIN drivers AS d "
                + "ON cd.driver_id = d.id "
                + "WHERE c.id = ? "
                + "AND c.is_deleted = FALSE "
                + "AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatement =
                        connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of drivers by id "
                    + id, throwable);
        }
    }

    private Car insertCarsDrivers(Car car) {
        List<Driver> drivers = car.getDrivers();
        String query = "INSERT INTO cars_drivers (`driver_id`, `car_id`) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarsDriversStatement =
                        connection.prepareStatement(query)) {
            updateCarsDriversStatement.setLong(2, car.getId());
            for (Driver driver : drivers) {
                updateCarsDriversStatement.setLong(1, driver.getId());
                updateCarsDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a car from cars_drivers "
                    + car, throwable);
        }
        return car;
    }

    private Car parseManufactureWithCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
