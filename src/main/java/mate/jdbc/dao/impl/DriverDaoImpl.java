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
        String insertDriverRequest = "INSERT INTO drivers(name, license_number) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnect();
                 PreparedStatement insertDriverStatement =
                         connection.prepareStatement(insertDriverRequest,
                                 Statement.RETURN_GENERATED_KEYS)) {
            insertDriverStatement.setString(1, driver.getName());
            insertDriverStatement.setString(2, driver.getLicenseNumber());
            insertDriverStatement.executeUpdate();
            ResultSet resultSet = insertDriverStatement.getGeneratedKeys();
            if (resultSet.next()) {
                driver.setId(resultSet.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver"
                    + driver + "  to DB", e);
        }
        return driver;
    }

    @Override
    public Optional<Driver> get(Long id) {
        String getDriverByIdRequest = "SELECT * FROM drivers "
                + "WHERE is_deleted = false AND id = ?;";
        Driver driver = null;
        try (Connection connection = ConnectionUtil.getConnect();
                 PreparedStatement getDriverByIdStatement = connection
                         .prepareStatement(getDriverByIdRequest)) {
            getDriverByIdStatement.setLong(1, id);
            ResultSet resultSet = getDriverByIdStatement.executeQuery();
            while (resultSet.next()) {
                driver = parseResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers "
                    + driver + "  to DB", e);
        }
        return Optional.ofNullable(driver);
    }

    @Override
    public List<Driver> getAll() {
        String getAllDriversRequest = "SELECT * FROM drivers WHERE is_deleted = false;";
        List<Driver> allDriversList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnect();
                  PreparedStatement getAllDriversStatement = connection
                             .prepareStatement(getAllDriversRequest)) {
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                allDriversList.add(parseResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all Drivers from DB", e);
        }
        return allDriversList;
    }

    @Override
    public Driver update(Driver driver) {
        String updateDriverRequest = "UPDATE drivers SET name = ?, license_number = ? "
                + "WHERE is_deleted = false AND id  = ?;";
        String getDriverByIdRequest = "SELECT * FROM drivers "
                + "WHERE is_deleted = false AND id = ?;";
        try (Connection connection = ConnectionUtil.getConnect();
                 PreparedStatement updateDriverStatement =
                         connection.prepareStatement(updateDriverRequest);
                 PreparedStatement getDriverByIdStatement =
                         connection.prepareStatement(getDriverByIdRequest)) {
            Driver oldDriver = null;
            getDriverByIdStatement.setLong(1, driver.getId());
            ResultSet resultSet = getDriverByIdStatement.executeQuery();
            while (resultSet.next()) {
                oldDriver = parseResultSet(resultSet);
            }
            updateDriverStatement.setString(1, driver.getName());
            updateDriverStatement.setString(2, driver.getLicenseNumber());
            updateDriverStatement.setLong(3, driver.getId());
            updateDriverStatement.executeUpdate();
            return oldDriver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update driver by id "
                    + driver.getId() + " from DB", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE drivers SET is_deleted = true WHERE id  = ?;";
        try (Connection connection = ConnectionUtil.getConnect();
                 PreparedStatement deleteStatement =
                         connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver by id "
                    + id + " from DB", e);
        }
    }

    private Driver parseResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }
}
