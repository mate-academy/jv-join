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
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {
    @Override
    public Manufacturer create(Manufacturer manufacturer) {
 hw
        String insertRequest = "INSERT INTO manufacturers (manufacturer_name,manufacturer_country) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createManufacturerStatement
                        = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            setUpdate(createManufacturerStatement, manufacturer).executeUpdate();
            ResultSet resultSet = createManufacturerStatement.getGeneratedKeys();

        String query = "INSERT INTO manufacturers (name, country) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveManufacturerStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            saveManufacturerStatement.setString(1, manufacturer.getName());
            saveManufacturerStatement.setString(2, manufacturer.getCountry());
            saveManufacturerStatement.executeUpdate();
            ResultSet resultSet = saveManufacturerStatement.getGeneratedKeys();
 master
            if (resultSet.next()) {
                manufacturer.setId(resultSet.getObject(1, Long.class));
            }
            return manufacturer;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create manufacturer. " + manufacturer + " ",
                    throwable);
        }
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
 hw
        String selectRequest = "SELECT * FROM manufacturers WHERE manufacturer_id = ? "
                + "AND deleted = false;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerStatement =
                        connection.prepareStatement(selectRequest)) {

        String query = "SELECT * FROM manufacturers "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerStatement = connection.prepareStatement(query)) {
 master
            getManufacturerStatement.setLong(1, id);
            ResultSet resultSet = getManufacturerStatement.executeQuery();
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                manufacturer = getManufacturer(resultSet);
            }
            return Optional.ofNullable(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer by id. Id = " + id, e);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
 hw
        String selectRequest = "SELECT * FROM manufacturers WHERE deleted = FALSE";
        try (
                Connection connection = ConnectionUtil.getConnection();
                Statement getManufacturersStatement
                        = connection.createStatement()) {
            List<Manufacturer> manufacturers = new ArrayList<>();
            ResultSet resultSet = getManufacturersStatement.executeQuery(selectRequest);

        String query = "SELECT * FROM manufacturers WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllManufacturersStatement
                        = connection.prepareStatement(query)) {
            List<Manufacturer> manufacturers = new ArrayList<>();
            ResultSet resultSet = getAllManufacturersStatement.executeQuery();
 master
            while (resultSet.next()) {
                manufacturers.add(getManufacturer(resultSet));
            }
            return manufacturers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of manufacturers "
                    + "from manufacturers table. ",
                    throwable);
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
 hw
        String updateRequest = "UPDATE manufacturers SET manufacturer_name = ?, "
                + "manufacturer_country = ? WHERE manufacturer_id = ? "
                + "AND deleted = FALSE";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateManufacturerStatement
                        = setUpdate(connection.prepareStatement(updateRequest), manufacturer)) {

        String query = "UPDATE manufacturers SET name = ?, country = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateManufacturerStatement
                        = connection.prepareStatement(query)) {
            updateManufacturerStatement.setString(1, manufacturer.getName());
            updateManufacturerStatement.setString(2, manufacturer.getCountry());
 master
            updateManufacturerStatement.setLong(3, manufacturer.getId());
            updateManufacturerStatement.executeUpdate();
            return manufacturer;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a manufacturer "
                    + manufacturer + " ", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
 hw
        String deleteRequest = "UPDATE manufacturers SET deleted = TRUE WHERE manufacturer_id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteManufacturerStatement =
                        connection.prepareStatement(deleteRequest)) {

        String query = "UPDATE manufacturers SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteManufacturerStatement
                        = connection.prepareStatement(query)) {
 master
            deleteManufacturerStatement.setLong(1, id);
            return deleteManufacturerStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a manufacturer by id " + id + " ",
                    throwable);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(newId);
        return manufacturer;
    }
}
