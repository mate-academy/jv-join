package mate.jdbc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mate.jdbc.service.FileReaderServiceImpl;

public class ResetTablesUtil {
    private static final String INIT_DB_SQL_FILENAME = "src/main/resources/init_db.sql";

    public static void resetTablesToInitialState() {
        String query = new FileReaderServiceImpl()
                .readCreateTableQueryFromFile(INIT_DB_SQL_FILENAME);
        /*String query = "CREATE TABLE `manufacturers` (
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(225) NOT NULL,
    `country` VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);"*/
        System.out.println(query);
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't reset DB to initial state", e);
        }
    }
}
