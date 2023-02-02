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
        String carCreateQuery = "INSERT INTO `taxi_service`.`cars` (`model`, `manufacturer_id`) "
                + "VALUES (?, ?);";
        try (
                Connection connection = ConnectionUtil
                        .getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(carCreateQuery, Statement.RETURN_GENERATED_KEYS)
                ) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + "." + e);
        }
        setDriversByCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuestion = "SELECT c.id, c.model, c.manufacturer_id,"
                + " m.name AS manufacturer, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND c.id = ?;";
        Car car = null;
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getCarQuestion)
                ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id:" + id + ".", e);
        }
        car.setDrivers(getAllDriversForCar(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getCarQuery = "SELECT c.id, c.model, c.manufacturer_id,"
                + " m.name AS manufacturer, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getCarQuery)
                ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars list.", e);
        }
        for (Car car: cars) {
            car.setDrivers(getAllDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateQuery)
                ) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + ".", e);
        }
        deleteDriversByCar(car);
        setDriversByCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)
                ) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id:" + id + ".", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String getAllQuery = "SELECT c.id, c.model, c.manufacturer_id,"
                + " m.name AS manufacturer, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllQuery)
                ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all car to the driver "
                    + "with id:" + id, e);
        }
        return cars;
    }

    private void setDriversByCar(Car car) {
        String query = "INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (?, ?)";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(query)
                ) {
            for (Driver driver: car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't set drivers for " + car + ".",e);
        }
    }

    private void deleteDriversByCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(query)
        ) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers to the " + car + ".", e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getLong("manufacturer_id");
        String manufacturerName = resultSet.getString("manufacturer");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName, country);
        Long carId = resultSet.getLong("id");
        String model = resultSet.getString("model");
        return new Car(carId, model, manufacturer);
    }

    private List<Driver> getAllDriversForCar(Long id) {
        String getAllDriversQuery = "SELECT d.id, d.name,  d.license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDriversQuery)
                ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers for car by id:"
                    + id + ".", e);
        }
    }
}
