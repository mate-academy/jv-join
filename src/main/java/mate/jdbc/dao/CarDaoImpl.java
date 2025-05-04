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
    public Optional<Car> create(Car car) {
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertLinksBetweenCarAndDriver(car);
        return Optional.ofNullable(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS id, m.id AS manufacturer_id, model, "
                + " m.name AS name, m.country AS country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }

        if (car != null) {
            car.setDrivers(getAllByCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS id, m.id AS manufacturer_id, model,"
                + " m.name AS name, m.country AS country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of cars ", e);
        }
        for (Car car : carList) {
            car.setDrivers(getAllByCar(car.getId()));
        }
        return carList;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS id, m.id AS manufacturer_id, model,"
                + " m.name AS name, m.country AS country "
                + "FROM cars c INNER JOIN cars_drivers cd  ON c.id = cd.car_id "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of cars by id " + driverId, e);
        }
        for (Car car : carList) {
            car.setDrivers(getAllByCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ?"
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        deleteLinksBetweenCarAndDriver(car.getId());
        insertLinksBetweenCarAndDriver(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        boolean isResult;
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(query)) {
            statement.setLong(1, id);
            isResult = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
        deleteLinksBetweenCarAndDriver(id);
        return isResult;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet
                .getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
        return new Car(resultSet.getObject("id", Long.class),
                resultSet.getString("model"), manufacturer, null);
    }

    private List<Driver> getAllByCar(Long carId) {
        String getAllDriverForCar = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers ca ON d.id = ca.driver_id WHERE ca.car_id = ?;";
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(getAllDriverForCar)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(parserDriverFromResultSet(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id " + carId, e);
        }
    }

    private Driver parserDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void deleteLinksBetweenCarAndDriver(Long carId) {
        String oldLinks = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(oldLinks)) {
            statement.setLong(1, carId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete "
                    + carId + " in cars_drivers table.", e);
        }
    }

    private void insertLinksBetweenCarAndDriver(Car car) {
        String addNewLinks = "INSERT INTO cars_drivers(driver_id, car_id) VALUES(?, ?)";
        try (PreparedStatement statement = ConnectionUtil
                .getConnection().prepareStatement(addNewLinks)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add new links by "
                    + car.getId() + " in cars_drivers table.", e);
        }
    }
}
