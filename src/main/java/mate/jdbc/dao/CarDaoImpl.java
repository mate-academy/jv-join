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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES  (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection
                         .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
            // we should insert a list of drivers here???
        } catch (SQLException throwables) {
            throw new RuntimeException("cant create such a car" + car, throwables);
        }
        insertCarsDriversRecords(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, m.id AS m_id, m.name, m.country"
                + " FROM cars c JOIN manufacturers m ON manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResult(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(parseDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.model, c.manufacturer_id, m.id AS m_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = 0";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car localCar = getCarFromResult(resultSet);
                cars.add(localCar);
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("cant get the list of all cars!", throwables);
        }
        if (cars != null) {
            for (Car car : cars) {
                car.setDrivers(parseDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.model, c.manufacturer_id, m.id AS m_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE c.is_deleted = 0 and cd.driver_id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car localCar = getCarFromResult(resultSet);
                cars.add(localCar);
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("cant get the list of all cars!", throwables);
        }
        if (cars != null) {
            cars.forEach(c -> c.setDrivers(parseDriversForCar(c.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("cant update chosen car: " + car.getId(), throwables);
        }
        deleteCarDriversRecords(car.getId());
        insertCarsDriversRecords(car);
        return car;
    }

    private void deleteCarDriversRecords(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("cant delete drivers in car: " + id, throwables);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int numberDeletedRows = statement.executeUpdate();
            return numberDeletedRows != 0;
        } catch (SQLException throwables) {
            throw new RuntimeException("can't delete car with id: " + id, throwables);
        }
    }

    private List<Driver> parseDriversForCar(Long id) {
        String getDriverQuery = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getDriverQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new RuntimeException("cant parse Drivers for car with id: " + id, throwables);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertCarsDriversRecords(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?) ";
        for (Driver driver : car.getDrivers()) {
            try (
                    Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);
            ) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throw new RuntimeException("cant insert driver to cars_drivers: car_id = "
                        + car.getId(), throwables);
            }
        }
    }

    private Car getCarFromResult(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getString("name"),
                resultSet.getString("country")
        );
        manufacturer.setId(resultSet.getLong("m_id"));
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setManufacturer(manufacturer);
        return car;
    }
}
