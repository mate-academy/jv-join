package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        String query = "INSERT INTO cars(manufacturer_id, model)"
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(
                        query, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t add Car to DB", e);
        }
        for (Driver driver : car.getDrivers()) {
            setCarDriversRelations(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT m.id, m.name,"
                + " m.country, c.id AS car_id, c.model "
                + "FROM manufacturers m  LEFT JOIN cars c "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? "
                + "AND c.deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = createCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get Car from DB with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversList(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT m.id, m.name, m.country, c.id AS car_id, c.model "
                + "FROM manufacturers m "
                + "LEFT JOIN cars c "
                + "ON m.id = c.manufacturer_id;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all Cars from DB", e);
        }
        for (Car car: cars) {
            car.setDrivers(getAllDriversList(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, "
                + "manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            if (updateStatement.executeUpdate() < 1) {
                throw new NoSuchElementException("Can`t find element by car" + car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        deleteCarsDriversRelations(car.getId());
        for (Driver driver : car.getDrivers()) {
            setCarDriversRelations(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = true "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete Car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.manufacturer_id AS id, c.model, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN cars_drivers cd ON c.id = cd.car_id "
                + "INNER JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get List of all drivers by id " + driverId, e);
        }
        for (Car car: cars) {
            car.setDrivers(getAllDriversList(car.getId()));
        }
        return cars;
    }

    private Car createCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getString("name"),
                resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("id", Long.class));
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getAllDriversList(Long id) {
        String query = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "LEFT JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? "
                + "AND deleted = false;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection.prepareStatement(query)) {
            getAllDriversStatement.setLong(1, id);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(createDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get List of all Drivers by id " + id, e);
        }
        return drivers;
    }

    private Driver createDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }

    private void setCarDriversRelations(Long carId, Long driverId) {
        String query = "INSERT INTO cars_drivers(driver_id, car_id)"
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            preparedStatement.setLong(2, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t add dependency in cars_drivers Table", e);
        }
    }

    private void deleteCarsDriversRelations(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete cars_drivers dependency for car id "
                    + id, e);
        }
    }
}
