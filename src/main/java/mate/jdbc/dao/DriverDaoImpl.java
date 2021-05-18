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
        String insertRequest = "INSERT INTO drivers (name, license_number) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement =
                        connection.prepareStatement(insertRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, driver.getName());
            createStatement.setString(2, driver.getLicenseNumber());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                driver.setId(generatedKeys.getObject(1, Long.class));
            }
            return driver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver: "
                    + driver.getName() + "to DB ", e);
        }
    }

    @Override
    public Optional<Driver> get(Long id) {
        String getRequest =
                "SELECT * FROM drivers WHERE id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement =
                        connection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Driver driver = null;
            if (resultSet.next()) {
                driver = getDriver(resultSet);
            }
            return Optional.ofNullable(driver);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get driver by id: "
                    + id + " from DB", e);
        }
    }

    @Override
    public List<Driver> getAll() {
        String getAllRequest = "SELECT * FROM drivers WHERE is_deleted = false";
        List<Driver> allDrivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet =
                    getAllStatement.executeQuery(getAllRequest);
            while (resultSet.next()) {
                allDrivers.add(getDriver(resultSet));
            }
            return allDrivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all drivers from DB", e);
        }
    }

    @Override
    public Driver update(Driver driver) {
        String updateRequest = "UPDATE drivers SET name = ?, "
                + "license_number = ? WHERE id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement =
                        connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, driver.getName());
            updateStatement.setString(2, driver.getLicenseNumber());
            updateStatement.setLong(3, driver.getId());
            if (updateStatement.executeUpdate() > 0) {
                return driver;
            }
            throw new DataProcessingException("No such driver " + driver.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update driver by name: "
                    + driver.getName() + " from DB", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE drivers SET is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete driver by id: "
                    + id + " from DB", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver =
                new Driver(resultSet.getString("name"),
                        resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }
}
