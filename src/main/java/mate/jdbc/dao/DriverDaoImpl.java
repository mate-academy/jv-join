package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class DriverDaoImpl implements DriverDao {
    @Override
    public Driver create(Driver driver) {
        String insertDriverRequest = "INSERT INTO drivers (name, licenseNumber) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                        = connection.prepareStatement(insertDriverRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, driver.getName());
            insertStatement.setString(2, driver.getLicenseNumber());
            insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) {
                driver.setId(resultSet.getObject(1, Long.class));
            }
            return driver;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create driver. " + driver + " ",
                    throwable);
        }
    }

    @Override
    public Optional<Driver> get(Long id) {
        String insertManufacturerRequest = "SELECT * FROM drivers"
                + " WHERE id = (?) AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement
                         = connection.prepareStatement(insertManufacturerRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Driver driver = null;
            if (resultSet.next()) {
                driver = getDriver(resultSet);
            }
            return Optional.ofNullable(driver);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get drivers by id " + id + " ",
                    throwable);
        }
    }

    @Override
    public List<Driver> getAll() {
        String insertManufacturerRequest = "SELECT * FROM drivers WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement
                        = connection.prepareStatement(insertManufacturerRequest)) {
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers "
                    + "from drivers table. ",
                    throwable);
        }
    }

    @Override
    public Driver update(Driver driver) {
        String insertManufacturerRequest = "UPDATE drivers SET name = ?, licenseNumber = ?"
                + " WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(insertManufacturerRequest)) {
            updateStatement.setString(1, driver.getName());
            updateStatement.setString(2, driver.getLicenseNumber());
            updateStatement.setLong(3, driver.getId());
            updateStatement.executeUpdate();
            return driver;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a driver "
                    + driver + " ", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String insertDriversRequest = "UPDATE drivers SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement
                        = connection.prepareStatement(insertDriversRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a drivers by id " + id + " ",
                    throwable);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("licenseNumber");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
}
