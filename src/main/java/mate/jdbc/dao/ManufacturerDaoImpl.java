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
        String query = "INSERT INTO manufacturers (manufacturer_name,manufacturer_country) "
                + "VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setUpdate(preparedStatement, manufacturer).executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
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
        String query = "SELECT manufacturer_id, manufacturer_name, "
                + "manufacturer_country FROM manufacturers"
                + " WHERE manufacturer_id = " + id + " AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                manufacturer = setManufacturer(resultSet);
            }
            return Optional.ofNullable(manufacturer);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get manufacturer by id " + id + " ",
                    throwable);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        String query = "SELECT * FROM manufacturers WHERE deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement statement = connection.createStatement()) {
            List<Manufacturer> manufacturers = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                manufacturers.add(setManufacturer(resultSet));
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
        String query = "UPDATE manufacturers SET manufacturer_name = ?, manufacturer_country = ?"
                + " WHERE manufacturer_id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement
                        = setUpdate(connection.prepareStatement(query), manufacturer)) {
            preparedStatement.setLong(3, manufacturer.getId());
            preparedStatement.executeUpdate();
            return manufacturer;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a manufacturer "
                    + manufacturer + " ", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE manufacturers SET deleted = TRUE WHERE manufacturer_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a manufacturer by id " + id + " ",
                    throwable);
        }
    }

    private Manufacturer setManufacturer(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(newId);
        return manufacturer;
    }

    private PreparedStatement setUpdate(PreparedStatement statement,
                                        Manufacturer manufacturer) throws SQLException {
        statement.setString(1, manufacturer.getName());
        statement.setString(2, manufacturer.getCountry());
        return statement;
    }
}
