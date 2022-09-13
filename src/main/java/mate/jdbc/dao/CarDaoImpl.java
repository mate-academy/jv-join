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
        String query = "INSERT INTO cars (model, manufacturers_id) "
                + "VALUES (?, ?)";
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
            throw new DataProcessingException("Couldn't create сar. " + car, e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, model, manufacturers_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturers_id = m.id "
                + "WHERE c.is_deleted = FALSE AND c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id, "
                + "model, manufacturers_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturers_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection
                         .prepareStatement(selectRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars", e);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturers_id = ? "
                + "WHERE is_deleted = FALSE AND id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement = connection
                         .prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car = " + car, e);
        }
        deleteDriversForCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, model, manufacturers_id, name, country "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturers_id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers by driver id: " + driverId, e);
        }
        for (Car car: cars) {
            car.setDrivers(getDriversForCar(driverId));
        }
        return cars;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection
                         .prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id = " + id, e);
        }
    }

    private void addDriversToCar(Car car) {
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
            throw new DataProcessingException("Couldn't add drivers for car "
                    + car + ". ", e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                               + "FROM drivers d JOIN cars_drivers cd "
                               + "ON d.id = cd.driver_id "
                               + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversForCarStatement = connection
                         .prepareStatement(getAllDriversForCarRequest)) {
            getDriversForCarStatement.setLong(1, id);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car by id = "
                    + id + ". ", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void deleteDriversForCar(Car car) {
        String deleteRelationsRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteRelationsStatement = connection
                         .prepareStatement(deleteRelationsRequest)) {
            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations with drivers for car = "
                    + car, e);
        }
    }
}
