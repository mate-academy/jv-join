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
        String insertQuery = "INSERT INTO cars(model, manufacturer) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertStatement =
                         connection.prepareStatement(insertQuery,
                                 Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, car.getModel());
            insertStatement.setLong(2, car.getManufacturer().getId());
            insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create car. " + car, throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car with id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversListForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer = m.id"
                + " WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all cars from cars table.", throwable);
        }
        if (!cars.isEmpty()) {
            cars.forEach(car -> car.setDrivers(getDriversListForCar(car.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        boolean isUpdated;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setObject(2, car.getManufacturer().getId());
            updateStatement.setObject(3, car.getId());
            isUpdated = updateStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't update car " + car
                + "in the cars table.", throwable);
        }
        if (isUpdated) {
            deleteDrivers(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setObject(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete car with id "
                    + id + ".", throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.id, c.model, c.manufacturer, "
                + "m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllByDriverStatement =
                            connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all cars by driver with id "
            + driverId + ".",throwable);
        }
        if (!cars.isEmpty()) {
            cars.forEach(car -> car.setDrivers(getDriversListForCar(car.getId())));
        }
        return cars;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        return manufacturer;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel(model);
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriversListForCar(Long carId) {
        String getDriversListForCarQuery = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversListForCarStatement =
                        connection.prepareStatement(getDriversListForCarQuery)) {
            getDriversListForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversListForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get driver's list for car with id "
                    + carId + ".", throwable);
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
        String insertDriversQuery = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement =
                            connection.prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert drivers into car "
                    + car + ".", throwable);
        }
    }

    private void deleteDrivers(Car car) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement =
                         connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete car " + car
                    + "from cars_drivers table.", throwable);
        }
    }
}
