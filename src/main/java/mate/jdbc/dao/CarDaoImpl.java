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
        String query = "INSERT INTO cars (model, manufacturer_id)"
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        return createCarDrivers(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE ;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars list ", e);
        }
        cars.forEach(car -> car.setDrivers(getCarDrivers(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        removeCarDrivers(car.getId());
        createCarDrivers(car);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        removeCarDrivers(id);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id" + id, e);
        }
    }

    @Override
    public List<Car> getCarsByDriverId(Long id) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.model, m.id, m.name, m.country FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id " + id, e);
        }
        cars.forEach(car -> car.setDrivers(getCarDrivers(car.getId())));
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject(1, Long.class);
        String carModel = resultSet.getString(2);
        Long manufacturerId = resultSet.getObject(3, Long.class);
        String manufacturerName = resultSet.getString(4);
        String manufacturerCountry = resultSet.getString(5);
        Manufacturer manufacturer
                = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        return new Car(carId, carModel, manufacturer);
    }

    private Car createCarDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id)"
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't attach drivers to car " + car, e);
        }
        return car;
    }

    private void removeCarDrivers(Long id) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers by car id" + id, e);
        }
    }

    private List<Driver> getCarDrivers(Long id) {
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM drivers d "
                + "INNER JOIN cars_drivers cd "
                + "ON cd.driver_id = d.id "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car id " + id, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(1, Long.class);
        String name = resultSet.getString(2);
        String country = resultSet.getString(3);
        return new Driver(id, name, country);
    }
}
