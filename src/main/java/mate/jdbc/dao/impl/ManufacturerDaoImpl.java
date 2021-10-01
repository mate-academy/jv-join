package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {
    @Override
    public List<Manufacturer> getAll() {
        List<Manufacturer> manufacturerList = new ArrayList<>();
        String getAllManufacturersRequest
                = "SELECT * FROM manufacturers WHERE is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllManufacturersStatement
                        = connection.prepareStatement(getAllManufacturersRequest)) {
            ResultSet resultSet = getAllManufacturersStatement
                    .executeQuery();
            while (resultSet.next()) {
                manufacturerList.add(getManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all manufacturers from DB.", e);
        }
        return manufacturerList;
    }

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        String insertManufacturerRequest
                = "INSERT INTO manufacturers(name, country) values(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createManufacturerStatement =
                         connection.prepareStatement(insertManufacturerRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            createManufacturerStatement.setString(1, manufacturer.getName());
            createManufacturerStatement.setString(2, manufacturer.getCountry());
            createManufacturerStatement.executeUpdate();
            ResultSet generatedKeys = createManufacturerStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert manufacturer "
                    + manufacturer + " to DB.", e);
        }
        return manufacturer;
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String updateManufacturerRequest
                = "UPDATE manufacturers SET name=?,country=? WHERE id=?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateManufacturerStatement =
                         connection.prepareStatement(updateManufacturerRequest)) {
            updateManufacturerStatement.setString(1, manufacturer.getName());
            updateManufacturerStatement.setString(2, manufacturer.getCountry());
            updateManufacturerStatement.setObject(3, manufacturer.getId());
            updateManufacturerStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update manufacturer "
                    + manufacturer + " in DB.", e);
        }
        return manufacturer;
    }

    @Override
    public boolean delete(Long id) {
        String deleteManufacturerRequest
                = "UPDATE manufacturers SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteManufacturerStatement =
                         connection.prepareStatement(deleteManufacturerRequest)) {
            deleteManufacturerStatement.setLong(1, id);
            return deleteManufacturerStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete manufacturer with id = "
                    + id + " from DB.", e);
        }
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        Manufacturer manufacturer = null;
        String getManufacturerRequest
                = "SELECT * FROM manufacturers WHERE is_deleted = false AND id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getManufacturerStatement =
                         connection.prepareStatement(getManufacturerRequest)) {
            getManufacturerStatement.setLong(1, id);
            ResultSet resultSet = getManufacturerStatement.executeQuery();
            if (resultSet.next()) {
                manufacturer = getManufacturer(resultSet);
            }
            return Optional.ofNullable(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer with id = "
                    + id + " from DB.", e);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        return new Manufacturer(id, name, country);
    }
}
