package mate.jdbc.dao.impl;

import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {
    private static final String CANT_GET_ALL_MESSAGE = "Can't get all manufacturers from DB.";
    private static final String CANT_CREATE_MESSAGE = "Can't insert manufacturer %s to DB.";
    private static final String CANT_UPDATE_MESSAGE = "Can't update manufacturer %s in DB.";
    private static final String CANT_DELETE_MESSAGE
            = "Can't delete manufacturer with id = %s from DB.";
    private static final String CANT_GET_MESSAGE = "Can't get manufacturer with id = %s from DB.";
    private static final String NAME = "name";
    private static final String COUNTRY = "country";
    private static final String ID = "id";

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
            throw new RuntimeException(CANT_GET_ALL_MESSAGE, e);
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
            throw new RuntimeException(String.format(CANT_CREATE_MESSAGE, manufacturer), e);
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
            throw new RuntimeException(String.format(CANT_UPDATE_MESSAGE, manufacturer), e);
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
            throw new RuntimeException(String.format(CANT_DELETE_MESSAGE, id), e);
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
            throw new RuntimeException(String.format(CANT_GET_MESSAGE, id), e);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(ID, Long.class);
        String name = resultSet.getString(NAME);
        String country = resultSet.getString(COUNTRY);
        return new Manufacturer(id, name, country);
    }
}
