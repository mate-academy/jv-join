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
        String createQuery = "INSERT INTO cars(model, manufacturers_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add car to database " + car, e);
        }
        insertNewRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS car_id,"
                + " c.model AS car_model, c.manufacturers_id AS manufacturer_id,"
                + " m.country AS manufacturer_country, m.name As manufacturer_name"
                + " FROM cars c JOIN manufacturers m ON c.manufacturers_id = m.id "
                + " WHERE c.id = ? AND c.is_deleted = false AND m.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = genCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversListFromResultSet(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    private Car genCarFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = Manufacturer.builder()
                .name(resultSet.getString("manufacturer_name"))
                .country(resultSet.getString("manufacturer_country"))
                .id(resultSet.getObject("manufacturer_id", Long.class))
                .build();
        return Car.builder()
                .id(resultSet.getObject("car_id", Long.class))
                .manufacturer(manufacturer)
                .model(resultSet.getString("car_model"))
                .build();
    }

    private List<Driver> getDriversListFromResultSet(Long carId) {
        String getQuery = "SELECT * FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE car_id = ? AND d.is_deleted = false;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getQuery)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers list from db");
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return Driver.builder()
                .licenseNumber(resultSet.getString("license_number"))
                .name(resultSet.getString("name"))
                .id(resultSet.getObject(1, Long.class))
                .build();
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id,"
                + " c.model AS car_model, c.manufacturers_id AS manufacturer_id,"
                + " m.country AS manufacturer_country, m.name As manufacturer_name"
                + " FROM cars c JOIN manufacturers m ON c.manufacturers_id = m.id "
                + " WHERE c.is_deleted = false AND m.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(genCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from db", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversListFromResultSet(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturers_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car + " in taxi_service_db", e);
        }
        deleteRelations(car);
        insertNewRelations(car);
        return car;
    }

    private boolean deleteRelations(Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteRelationsQuery)) {
            statement.setLong(1, car.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car drivers "
                    + car + " in taxi_service_db", e);
        }
    }

    private void insertNewRelations(Car car) {
        String insertNewRelationsQuery =
                "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertNewRelationsQuery)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car drivers "
                    + car + " in taxi_service_db", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.id AS car_id,"
                + " c.model AS car_model, c.manufacturers_id AS manufacturer_id,"
                + " m.country AS manufacturer_country, m.name As manufacturer_name"
                + " FROM cars c JOIN manufacturers m ON c.manufacturers_id = m.id"
                + " JOIN cars_drivers cd on c.id = cd.car_id"
                + " WHERE driver_id = ? AND c.is_deleted = false AND m.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllByDriverQuery)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(genCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversListFromResultSet(car.getId()));
        }
        return cars;
    }
}
