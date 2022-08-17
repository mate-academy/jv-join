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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car.getDrivers());
        insertAllRelationsCarWithDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, model, manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND c.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = createCar(resultSet);
                car.setManufacturer(createManufacturer(resultSet));
                car.setDrivers(getDrivers(id));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement("SELECT c.id, "
                        + "model, manufacturer_id, name, country "
                        + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                        + "WHERE c.is_deleted = false")) {
            ResultSet getAllResult = getAllStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (getAllResult.next()) {
                Car car = createCar(getAllResult);
                car.setManufacturer(createManufacturer(getAllResult));
                car.setDrivers(getDrivers(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, "
                + "manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
            deleteAllRelationsCarWithDrivers(car.getId());
            insertAllRelationsCarWithDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String query = "SELECT car_id FROM cars_drivers WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            getAllStatement.setLong(1, id);
            ResultSet getAllResult = getAllStatement.executeQuery();
            List<Car> allCars = new ArrayList<>();
            while (getAllResult.next()) {
                Long carId = getAllResult.getLong(1);
                allCars.add(get(carId)
                        .orElseThrow(() -> new NoSuchElementException("Can`t get car "
                                + "by id = " + carId)));
            }
            return allCars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car by driver_id " + id, e);
        }
    }

    private void insertDrivers(List<Driver> drivers) {
        String query = "INSERT INTO drivers (name, license_number) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            for (Driver driver : drivers) {
                statement.setString(1, driver.getName());
                statement.setString(2, driver.getLicenseNumber());
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    driver.setId(resultSet.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create drivers "
                    + drivers, e);
        }
    }

    private List<Driver> getDrivers(Long id) {
        String query = "SELECT d.id, name, license_number FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = false AND cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultDrivers = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultDrivers.next()) {
                drivers.add(createDriver(resultDrivers));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get drivers by id = " + id, e);
        }
    }

    private Car createCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong(1));
        car.setModel(resultSet.getString(2));
        car.setDrivers(getDrivers(resultSet.getLong(1)));
        return car;
    }

    private Driver createDriver(ResultSet resultDrivers) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultDrivers.getLong(1));
        driver.setName(resultDrivers.getString(2));
        driver.setLicenseNumber(resultDrivers.getString(3));
        return driver;
    }

    private Manufacturer createManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong(3));
        manufacturer.setName(resultSet.getString(4));
        manufacturer.setCountry(resultSet.getString(5));
        return manufacturer;
    }

    private int deleteAllRelationsCarWithDrivers(Long idCar) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement
                        = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, idCar);
            return deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t deleted relations between drivers and car, "
                    + "by car_id = " + idCar, e);
        }
    }

    private boolean insertAllRelationsCarWithDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(query)) {
            insertStatement.setLong(1, car.getId());
            int numOfUpdatedLines = 0;
            List<Driver> drivers = car.getDrivers();
            for (Driver driver : drivers) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
                numOfUpdatedLines++;
            }
            return numOfUpdatedLines == drivers.size();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert relation cars with drivers. "
                    + car, e);
        }
    }
}
