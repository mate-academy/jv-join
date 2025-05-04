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
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement =
                        connection.prepareStatement(createRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert authors to car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.id, c.model, m.name, m.country, c.manufacturer_id "
                + "FROM cars c INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement =
                        connection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getRequest = "SELECT c.id, c.model, m.name, m.country, m.id as manufacturer_id "
                + "FROM cars as c INNER JOIN manufacturers as m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getRequest)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars", e);
        }
        if (!cars.isEmpty()) {
            cars.forEach(c -> c.setDrivers(getAllDriversForCar(c.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars "
                            + "SET model = ?, manufacturer_id = ? "
                            + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car " + car, e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    private void deleteDrivers(Car car) {
        String deleteRelationshipsRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(deleteRelationshipsRequest)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String request = "SELECT * FROM cars_drivers cd "
                + "JOIN cars c "
                + "ON c.id = cd.car_id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        ArrayList<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(request)) {
            statement.setObject(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars with driver id = "
                    + driverId, e);
        }
        for (Car car: cars) {
            car.setDrivers(getAllDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = parseManufacturerFromResultSet(resultSet);
        return new Car(id, model, manufacturer, null);
    }

    private Manufacturer parseManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        return new Manufacturer(id, name, country);
    }

    private List<Driver> getAllDriversForCar(Long carId) {
        String getAllDrivers = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDrivers)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drives from car with id = " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }
}
