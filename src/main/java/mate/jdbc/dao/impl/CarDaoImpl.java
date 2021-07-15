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
        String query = "INSERT INTO cars (manufacture_id, model) "
                + "VALUES (?, ?);";
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
        updateCarsDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.model, m.id, m.name, m.country, c.id AS car_id "
                + "FROM cars AS c JOIN manufacturers AS m ON c.manufacture_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement =
                         connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
                car.setManufacturer(parseManufactureFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        car.setDrivers(getDriversForCar(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id FROM taxi.cars WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarStatement =
                         connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(get(resultSet.getObject("id", Long.class)).get());
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars ", throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String queryCars = "UPDATE cars SET manufacture_id = ?, model = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(queryCars)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a car " + car, throwable);
        }
        return deleteFromCar(car);
    }

    private Car deleteFromCar(Car car) {
        String queryDeleteCarsDriver = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarsDriversStatement =
                     connection.prepareStatement(queryDeleteCarsDriver)) {
            deleteCarsDriversStatement.setLong(1, car.getId());
            deleteCarsDriversStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a car from cars_drivers "
                    + car, throwable);
        }
        updateCarsDrivers(car);
        return car;
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
        String query = "SELECT c.id FROM cars AS c "
                + " JOIN cars_drivers AS cd ON cd.car_id = c.id "
                + " JOIN drivers AS d ON cd.driver_id = d.id "
                + " WHERE d.id = ? AND "
                + " c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(get(resultSet.getObject("id", Long.class)).get());
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all by driver id "
                    + driverId, throwable);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id, d.name, d.license_number "
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
                Driver driver = parseDriversFromResultSet(resultSet);
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get list of drivers by id "
                    + id, throwable);
        }
    }

    private Car updateCarsDrivers(Car car) {
        List<Driver> drivers = car.getDrivers();
        String queryUpdateCarsDriver =
                "INSERT INTO `taxi`.`cars_drivers` (`driver_id`, `car_id`) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarsDriversStatement =
                         connection.prepareStatement(queryUpdateCarsDriver)) {
            if (drivers.size() > 0) {
                updateCarsDriversStatement.setLong(2, car.getId());
                for (Driver driver : drivers) {
                    updateCarsDriversStatement.setLong(1, driver.getId());
                    updateCarsDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a car from cars_drivers "
                    + car, throwable);
        }
        return car;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        return car;
    }

    private Manufacturer parseManufactureFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
