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
        String request = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String request = "SELECT c.id AS car_id, c.model, m.id "
                + "AS manufacturer_id, m.name "
                + "AS manufacturer_name, m.country "
                + "AS manufacturer_country "
                + "FROM cars c INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id "
                    + id + ". ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String request = "SELECT c.id AS car_id, c.model, "
                + "m.id AS manufacturer_id, m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB. ", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + "WHERE id = ? AND is_deleted = FALSE";
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());

            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();

            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(1, car.getId());
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String request = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id "
                    + id + ". ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String request = "SELECT c.id AS car_id, c.model, c.manufacturer_id, m.name "
                + "AS m_name, m.country AS manufacturer_country "
                + "FROM cars_drivers cd JOIN cars c ON cd.car_id = c.id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars list in DB by driver id "
                    + driverId + ". ", e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String request = "SELECT id, name, license_number FROM drivers JOIN cars_drivers"
                + " ON drivers.id = cars_drivers.driver_id WHERE cars_drivers.car_id = ?"
                + " AND is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car in DB by id "
                    + id + ". ", e);
        }
    }

    private Driver getDriversFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Long id = resultSet.getObject("id", Long.class);
        return new Driver(id, name, licenseNumber);
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver to car: " + car, e);
        }

    }
}
