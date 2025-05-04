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
        String createQuery = "INSERT INTO cars (model,manufacturer_id)"
                + " VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            addDriversToCar(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create car: " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars AS c "
                + "JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id:" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().getId());
            preparedStatement.setObject(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car:" + car, e);
        }
        removeDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String queryFroUpdate = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryFroUpdate)) {
            preparedStatement.setObject(1, id);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars AS c "
                + "JOIN cars_drivers AS cd ON cd.cars_id = c.id "
                + "JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE drivers_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all "
                    + "cars by driver`s id: " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (cars_id, drivers_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setObject(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t add drivers to car: " + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT * FROM drivers AS d "
                + "JOIN cars_drivers AS cd "
                + "ON d.id = cd.drivers_id "
                + "WHERE cd.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = getDriverFromResultSet(resultSet);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("drivers for car with id:" + carId, e);
        }
        return drivers;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private void removeDriversFromCar(Car car) {
        String queryForDelete = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queryForDelete)) {
            preparedStatement.setObject(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t remove drivers from car:" + car, e);
        }
    }
}
