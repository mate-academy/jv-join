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
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create Car. " + car, e);
        }
        if (car != null) {
            setDrivers(car);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        Car car = new Car();
        String query = "SELECT cars.id, cars.model, manufacturer_id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND manufacturers.is_deleted = FALSE "
                + "AND cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find car from DB by id = " + id, e);
        }
        car.setDrivers(getDrivers(id));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, manufacturer_id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND manufacturers.is_deleted = FALSE ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                car.setDrivers(getDrivers(car.getId()));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table.", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update driver in DB by car=" + car, e);
        }
        query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update driver in DB by car=" + car, e);
        }
        setDrivers(car);
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
            throw new DataProcessingException("Couldn't delete car FROM DB by id = " + id, e);
        }
    }

    private List<Driver> getDrivers(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String queryForDrivers = "SELECT name, license_number, driver_id FROM drivers "
                + "INNER JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryForDrivers)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                driver.setId(resultSet.getObject("driver_id", Long.class));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find any driver from DB by id=" + carId, e);
        }
        return drivers;
    }

    private void setDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot insert drivers into DB. All drivers: "
                    + car.getDrivers(), e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }
}
