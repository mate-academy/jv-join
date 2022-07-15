package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            insertRelationForCarQuery(car.getId(), car.getDrivers());
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT c.id AS id, c.model AS model, m.id AS manufacturer_id, "
                    + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                    + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                    + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }

        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                    + "SET model = ?, manufacturer_id = ? "
                    + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + " in carsDB.", e);
        }
        deleteAllRelationsForCarQuery(car.getId());
        if (car.getDrivers() != null) {
            insertRelationForCarQuery(car.getId(), car.getDrivers());
        }
        return car;
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
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id FROM cars "
                    + "JOIN cars_drivers cd ON cars.id = cd.car_id "
                    + "WHERE cd.driver_id = ? AND is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Car car = new Car(model, new ManufacturerDaoImpl()
                .get(resultSet.getObject("manufacturer_id", Long.class)).orElseThrow());
        car.setId(id);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND is_deleted = FALSE";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void deleteAllRelationsForCarQuery(Long id) {
        String deleteAllRelationsForCarQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteAllRelationsForCarQuery)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete relations fo car ID=" + id, e);
        }
    }

    private void insertRelationForCarQuery(Long carId, List<Driver> drivers) {
        String insertNewRelationForCarQuery = "INSERT INTO `cars_drivers` (`car_id`, `driver_id`) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(insertNewRelationForCarQuery)) {
            for (Driver driver : drivers) {
                statement.setLong(1, carId);
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert relations fo car ID=" + carId
                                                + " and list of drivers " + drivers, e);
        }
    }
}
