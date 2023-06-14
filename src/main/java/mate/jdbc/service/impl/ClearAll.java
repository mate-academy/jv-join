package mate.jdbc.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.util.ConnectionUtil;

public class ClearAll {
    public void clearAllFromTable() {
        String[] deleteRequests = {
                "DELETE FROM cars_drivers;",
                "DELETE FROM cars;",
                "ALTER TABLE cars AUTO_INCREMENT = 1;",
                "DELETE FROM drivers;",
                "ALTER TABLE drivers AUTO_INCREMENT = 1;",
                "DELETE FROM manufacturers;",
                "ALTER TABLE manufacturers AUTO_INCREMENT = 1;"
        };
        try (Connection connection = ConnectionUtil.getConnection()) {
            for (String request : deleteRequests) {
                try (PreparedStatement deleteStatement =
                             connection.prepareStatement(request)) {
                    deleteStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert the request", e);
        }
    }
}

