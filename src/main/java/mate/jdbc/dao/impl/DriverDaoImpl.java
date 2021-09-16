package mate.jdbc.dao.impl;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class DriverDaoImpl implements DriverDao {
    private static final String CANT_GET_ALL_MESSAGE = "Can't get all drivers from DB.";
    private static final String CANT_CREATE_MESSAGE = "Can't insert driver %s to DB.";
    private static final String CANT_UPDATE_MESSAGE = "Can't update driver %s in DB.";
    private static final String CANT_DELETE_MESSAGE = "Can't delete driver with id = %s from DB.";
    private static final String CANT_GET_MESSAGE = "Can't get driver with id = %s from DB.";
    private static final String NAME = "name";
    private static final String LICENCE_NUMBER = "licence_number";
    private static final String ID = "id";

    @Override
    public List<Driver> getAll() {
        List<Driver> driverList = new ArrayList<>();
        String getAllDriversRequest
                = "SELECT * FROM drivers WHERE is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversRequest)) {
            ResultSet resultSet = getAllDriversStatement
                    .executeQuery();
            while (resultSet.next()) {
                driverList.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(CANT_GET_ALL_MESSAGE, e);
        }
        return driverList;
    }

    @Override
    public Driver create(Driver driver) {
        String insertDriverRequest
                = "INSERT INTO drivers(name, licence_number) values(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createDriverStatement =
                        connection.prepareStatement(insertDriverRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            createDriverStatement.setString(1, driver.getName());
            createDriverStatement.setString(2, driver.getLicenseNumber());
            createDriverStatement.executeUpdate();
            ResultSet generatedKeys = createDriverStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                driver.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format(CANT_CREATE_MESSAGE, driver), e);
        }
        return driver;
    }

    @Override
    public Driver update(Driver driver) {
        String updateDriverRequest
                = "UPDATE drivers SET name=?,licence_number=? WHERE id=?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateDriverStatement =
                        connection.prepareStatement(updateDriverRequest)) {
            updateDriverStatement.setString(1, driver.getName());
            updateDriverStatement.setString(2, driver.getLicenseNumber());
            updateDriverStatement.setObject(3, driver.getId());
            updateDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(String.format(CANT_UPDATE_MESSAGE, driver), e);
        }
        return driver;
    }

    @Override
    public boolean delete(Long id) {
        String deleteDriverRequest
                = "UPDATE drivers SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteDriverStatement =
                         connection.prepareStatement(deleteDriverRequest)) {
            deleteDriverStatement.setLong(1, id);
            return deleteDriverStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new RuntimeException(String.format(CANT_DELETE_MESSAGE, id), e);
        }
    }

    @Override
    public Optional<Driver> get(Long id) {
        Driver driver = null;
        String getDriverRequest
                = "SELECT * FROM drivers WHERE is_deleted = false AND id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriverStatement =
                        connection.prepareStatement(getDriverRequest)) {
            getDriverStatement.setLong(1, id);
            ResultSet resultSet = getDriverStatement.executeQuery();
            if (resultSet.next()) {
                driver = getDriver(resultSet);
            }
            return Optional.ofNullable(driver);
        } catch (SQLException e) {
            throw new RuntimeException(String.format(CANT_GET_MESSAGE, id), e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(ID, Long.class);
        String name = resultSet.getString(NAME);
        String licenseNumber = resultSet.getString(LICENCE_NUMBER);
        return new Driver(id, name, licenseNumber);
    }
}
