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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
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
            throw new DataProcessingException("Can`t create car: " + car, e);
        }
        if (car.getDrivers() != null) {
            addDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars WHERE id = ? AND is_deleted = FALSE";
        Car result = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertToCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car with ID: " + id, e);
        }
        setAllFieldsForManufacturer(result.getManufacturer());
        result.setDrivers(getDrivers(id));
        return Optional.ofNullable(result);
    }

    @Override
    public List<Car> getAll() {
        List<Car> result = new ArrayList<>();
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(convertToCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t read all data from DB", e);
        }
        result.forEach(c -> {
            setAllFieldsForManufacturer(c.getManufacturer());
            c.setDrivers(getDrivers(c.getId()));
        });
        return result;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car with ID: " + car.getId(), e);
        }
        deleteDrivers(car.getId());
        addDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car with ID: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        List<Car> result = new ArrayList<>();
        String query = "SELECT id, model, manufacturer_id FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? "
                + "GROUP BY model;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(convertToCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver with ID: " + id, e);
        }
        result.forEach(c -> {
            setAllFieldsForManufacturer(c.getManufacturer());
            c.setDrivers(getDrivers(c.getId()));
        });
        return result;
    }

    private void setAllFieldsForManufacturer(Manufacturer manufacturer) {
        String query = "SELECT * FROM manufacturers WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, manufacturer.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get manufacturer for car from DB with ID: "
                    + manufacturer.getId(), e);
        }
    }

    private List<Driver> getDrivers(Long carId) {
        List<Driver> result = new ArrayList<>();
        String query = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? "
                + "GROUP BY name;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(convertToDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t read drivers for car from DB with ID: "
                    + carId, e);
        }
        return result;
    }

    private Driver convertToDriver(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private Car convertToCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void addDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t add drivers to cars_drivers with car: "
                    + car, e);
        }
    }

    private void deleteDrivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete data from cars_drivers with car_id: "
                    + id, e);
        }
    }
}
