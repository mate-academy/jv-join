package mate.jdbc.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.service.CreateDataBase;
import mate.jdbc.util.ConnectionUtil;

public class CreateDataBaseImpl implements CreateDataBase {

    @Override
    public void createdTable(String stringSql) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(stringSql);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to run script"
                    + stringSql, e);
        }
    }
}
