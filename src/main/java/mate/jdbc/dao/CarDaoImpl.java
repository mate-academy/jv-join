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
        String query = "INSERT INTO `cars` (model, manufacturers_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,car.getModel());
            preparedStatement.setLong(2,car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert car " + car + " to DB", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String query = "SELECT c.id c_id, c.model c_model, m.id m_id, "
                + "m.country m_country, m.name m_name FROM cars c LEFT JOIN "
                + "manufacturers m ON m.id = c.manufacturers_id WHERE c.id = ? "
                + "AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car from DB by car id " + id, e);
        }
        if (car != null) {
            car.setDriverList(getDriverForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String query = "SELECT c.id c_id, c.model c_model, m.id m_id,"
                + "m.country m_country, m.name m_name FROM cars c LEFT JOIN "
                + "manufacturers m ON m.id = c.manufacturers_id WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB", e);
        }
        for (Car car : carList) {
            car.setDriverList(getDriverForCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturers_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car" + car + " for DB", e);
        }
        deleteDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car from DB by car id " + id, e);
        }
    }

    @Override
    public List<Car> getAllCarsByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String query = "SELECT c.id c_id, c.model c_model, m.id m_id,"
                + " m.country m_country, m.name m_name"
                + " FROM cars_drivers LEFT JOIN cars c ON c.id = cars_drivers.cars_id"
                + " LEFT JOIN manufacturers m on c.manufacturers_id = m.id"
                + " WHERE drivers_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB by driver id "
                    + driverId, e);
        }
        return carList;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO `cars_drivers` (cars_id, drivers_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert all drivers to car: " + car, e);
        }
    }

    private List<Driver> getDriverForCar(Long id) {
        List<Driver> driverList = new ArrayList<>();
        String query = "SELECT d.id d_id, d.model d_model, d.licenseNumber d_license "
                    + "FROM cars_drivers cd LEFT JOIN drivers d on d.id = cd.drivers_id"
                    + " WHERE cd.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                driverList.add(parseDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all drivers from DB by car id " + id, e);
        }
        return driverList;
    }

    private void deleteDrivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement preparedStatement
                          = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete all drivers from DB by car id "
                    + id, e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("c_id", Long.class));
        car.setModel(resultSet.getString("c_model"));
        car.setManufacturer(parseManufacturer(resultSet));
        return car;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("m_id", Long.class));
        manufacturer.setName(resultSet.getString("m_name"));
        manufacturer.setCountry(resultSet.getString("m_country"));
        return manufacturer;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("d_id", Long.class));
        driver.setLicenseNumber(resultSet.getString("d_license"));
        driver.setName(resultSet.getString("d_model"));
        return driver;
    }
}
