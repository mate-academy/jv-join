package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement = connection.prepareStatement(createQuery,
                         Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car"
                    + car + ". ", e);
        }
        insertDriversToCarsDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT cars.id, model, manufacturers.id AS manufacturer_id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCarFromStatement(resultSet);
            }
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT cars.id, model, manufacturers.id AS manufacturer_id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromStatement(resultSet);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, id);
            int numberOfDeletedRows = statement.executeUpdate();
            return numberOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(updateQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            removeOldConnectionsInCarsDrivers(car);
            insertDriversToCarsDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT car_id "
                + "FROM cars_drivers "
                + "WHERE cars_drivers.driver_id = ? AND is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllByDriverStatement
                         = connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getObject("car_id", Long.class);
                Car car = get(carId).get();
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    private void insertDriversToCarsDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement
                         = connection.prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car"
                    + car + ". ", e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getListDriversQuery = "SELECT id, name, licenceNumber "
                + "FROM drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE "
                + "AND cars_drivers.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getListDriversQuery)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    private Driver getDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenceNumber"));
        return driver;
    }

    private Car getCarFromStatement(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        car.setId(id);
        car.setModel(model);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void removeOldConnectionsInCarsDrivers(Car car) {
        String removeConnectionsQuery = "UPDATE cars_drivers SET is_deleted = TRUE "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeConnectionsStatement
                         = connection.prepareStatement(removeConnectionsQuery)) {
            removeConnectionsStatement.setLong(1, car.getId());
            removeConnectionsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car"
                    + car + ". ", e);
        }
    }
}
