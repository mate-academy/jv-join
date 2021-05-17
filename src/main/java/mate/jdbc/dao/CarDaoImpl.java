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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car "
                    + car + ". ", throwable);
        }
        addDriversToDb(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.model, m.id as manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDriverList(getDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.model, m.name,"
                + "m.id as manufacturer_id, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars from db", e);
        }
        addDriversForCars(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND cars.deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            deleteDrivers(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Car cannot be update by id: " + car.getId(), e);
        }
        try {
            List<Driver> drivers = car.getDriverList();
            if (drivers != null) {
                for (Driver driver : drivers) {
                    addDriverForCar(car, driver);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot add drivers to list");
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement
                        = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Car cannot be deleted by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars_drivers cd "
                + "JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ? AND c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement
                         = connection.prepareStatement(query)) {
            getAllStatement.setLong(1, driverId);
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars by drivers id: " + driverId, e);
        }
        addDriversForCars(cars);
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(getManufacturer(resultSet.getObject("manufacturer_id", Long.class)));
        return car;
    }

    private Manufacturer getManufacturer(Long id) {
        String query = "SELECT * FROM manufacturers WHERE id = "
                + id + " AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                manufacturer = setManufacturer(resultSet);
            }
            return manufacturer;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get manufacturer by id " + id + " ",
                    throwable);
        }
    }

    private Manufacturer setManufacturer(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(newId);
        return manufacturer;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("name"),
                resultSet.getString("licence"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private List<Driver> getDrivers(Long id) {
        String query = "SELECT * FROM drivers d JOIN cars_drivers cd "
                + "ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                         connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriverFromResultSet(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get drivers for car id: " + id, e);
        }
    }

    private void addDriversForCars(List<Car> cars) {
        if (cars != null && cars.size() > 0) {
            for (Car car : cars) {
                car.setDriverList(getDrivers(car.getId()));
            }
        }
    }

    private void deleteDrivers(Car car) throws SQLException {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        }
    }

    private void addDriverForCar(Car car, Driver driver) throws SQLException {
        String query = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                         = connection.prepareStatement(query)) {
            insertStatement.setLong(1, car.getId());
            insertStatement.setLong(2, driver.getId());
            insertStatement.executeUpdate();
        }
    }

    private void addDriversToDb(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                         = connection.prepareStatement(query)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot insert drivers to the car by id:"
                    + car.getId(), e);
        }
    }
}
