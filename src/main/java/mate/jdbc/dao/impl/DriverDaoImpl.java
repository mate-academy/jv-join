package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class DriverDaoImpl implements DriverDao {
    @Override
    public Driver create(Driver driver) {
        String createDriver =
                "INSERT INTO drivers (name, license_number) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriver =
                        connection.prepareStatement(createDriver,
                            Statement.RETURN_GENERATED_KEYS)) {
            addDriver.setString(1, driver.getName());
            addDriver.setString(2, driver.getLicenseNumber());
            addDriver.executeUpdate();
            ResultSet resultSet = addDriver.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                driver.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver to DB " + driver, e);
        }
        return driver;
    }

    @Override
    public Optional<Driver> get(Long id) {
        String getDriver = "SELECT * FROM drivers WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverById = connection.prepareStatement(getDriver)) {
            getDriverById.setLong(1, id);
            ResultSet resultSet = getDriverById.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get driver by id = " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> getAll() {
        List<Driver> allDriver = new ArrayList<>();
        String getAllDriverQuery = "SELECT * FROM drivers WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDrivers =
                        connection.prepareStatement(getAllDriverQuery)) {
            ResultSet resultSet = getAllDrivers.executeQuery();
            while (resultSet.next()) {
                allDriver.add(createDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all drivers from DB", e);
        }
        return allDriver;
    }

    @Override
    public Driver update(Driver driver) {
        String updateDriver = "UPDATE drivers SET name = ?, license_number = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement update = connection.prepareStatement(updateDriver)) {
            update.setString(1, driver.getName());
            update.setString(2, driver.getLicenseNumber());
            update.setLong(3, driver.getId());
            update.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update driver to DB " + driver, e);
        }
        return driver;
    }

    @Override
    public boolean delete(Long id) {
        String deleteDriver =
                "UPDATE drivers SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deletedDriverById =
                        connection.prepareStatement(deleteDriver)) {
            deletedDriverById.setLong(1, id);
            return deletedDriverById.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t deleted driver by id " + id, e);
        }
    }

    private Driver createDriver(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number")
        );
    }
}
