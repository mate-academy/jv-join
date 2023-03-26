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
    public Manufacturer create(Manufacturer manufacturer) {
        String insertFormat =
                "INSERT INTO manufacturers (name, country) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addManufacturer =
                        connection.prepareStatement(insertFormat,
                             Statement.RETURN_GENERATED_KEYS)) {
            addManufacturer.setString(1, manufacturer.getName());
            addManufacturer.setString(2, manufacturer.getCountry());
            addManufacturer.executeUpdate();
            ResultSet generatedKeys = addManufacturer.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert manufacturer to DB " + manufacturer, e);
        }
        return manufacturer;
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        String getManufacturer = "SELECT * FROM manufacturers WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getData = connection.prepareStatement(getManufacturer)) {
            getData.setLong(1, id);
            ResultSet resultSet = getData.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get manufacturer from DB with id = " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Manufacturer> getAll() {
        String getAllManufacturerQuery = "SELECT * FROM manufacturers WHERE is_deleted = FALSE;";
        List<Manufacturer> allManufacturer = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllManufacturer =
                        connection.prepareStatement(getAllManufacturerQuery)) {
            ResultSet resultSet = getAllManufacturer.executeQuery(getAllManufacturerQuery);
            while (resultSet.next()) {
                allManufacturer.add(createManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all manufacturer from DB", e);
        }
        return allManufacturer;
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String updateId = "UPDATE manufacturers"
                + " SET name = ?, country = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement update = connection.prepareStatement(updateId)) {
            update.setString(1, manufacturer.getName());
            update.setString(2, manufacturer.getCountry());
            update.setLong(3, manufacturer.getId());
            update.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update manufacturer to DB" + manufacturer, e);
        }
        return manufacturer;
    }

    @Override
    public boolean delete(Long id) {
        String deleted = "UPDATE manufacturers SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement delete =
                        connection.prepareStatement(deleted)) {
            delete.setLong(1, id);
            return delete.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can`t deleted manufacturer from DB with id = " + id, e);
        }
    }

    private Manufacturer createManufacturer(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Long id = resultSet.getObject("id", Long.class);
        return new Manufacturer(name, country, id);
    }
}
