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
        String query = "INSERT INTO cars (manufacturer_id, model) "
                + "VALUES (?, ?)";
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

            query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
            PreparedStatement carsDriversStatement = connection.prepareStatement(query);

            List<Driver> drivers = car.getDrivers();
            carsDriversStatement.setLong(1, car.getId());
            for (Driver driver : drivers) {
                carsDriversStatement.setLong(2, driver.getId());
                carsDriversStatement.addBatch();
            }

            carsDriversStatement.executeUpdate();
            carsDriversStatement.close();

            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND m.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;

            if (resultSet.next()) {
                car = getCar(connection, resultSet);
            }

            return (car == null) ? Optional.empty() : Optional.of(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " AND m.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cars.add(getCar(connection, resultSet));
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars", e);
        }

        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();

            query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
            PreparedStatement driversStatement = connection.prepareStatement(query);

            List<Driver> drivers = car.getDrivers();
            for (Driver driver : drivers) {
                driversStatement.setLong(1, driver.getId());
                driversStatement.setLong(2, car.getId());
                driversStatement.addBatch();
            }

            driversStatement.executeUpdate();
            driversStatement.close();

            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("could not update car with id: " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " AND m.is_deleted = false "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(connection, resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not get all cars by driver with id: "
                + driverId, e);
        }

        return cars;
    }

    private Car getCar(Connection connection, ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(5, Long.class));
        manufacturer.setName(resultSet.getString(6));
        manufacturer.setCountry(resultSet.getString(7));

        car.setManufacturer(manufacturer);

        String query = "SELECT * FROM drivers d WHERE d.id IN"
                + " (SELECT driver_id FROM cars_drivers cd WHERE cd.car_id = ?)"
                + " AND d.is_deleted = FALSE";

        PreparedStatement driverStatement = connection.prepareStatement(query);
        driverStatement.setLong(1, car.getId());
        ResultSet driversResultSet = driverStatement.executeQuery();
        while (driversResultSet.next()) {
            car.getDrivers().add(getDriver(driversResultSet));
        }
        driverStatement.close();

        return car;
    }

    private Driver getDriver(ResultSet resultSet) {
        Driver driver = new Driver();
        try {
            driver.setId(resultSet.getObject(1, Long.class));
            driver.setName(resultSet.getString(2));
            driver.setLicenseNumber(resultSet.getString(3));
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't retrieve driver with id "
                + driver.getId(), e);
        }

        return driver;
    }
}
