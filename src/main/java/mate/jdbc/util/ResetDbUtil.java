package mate.jdbc.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import mate.jdbc.service.FileReaderServiceImpl;

public class ResetDbUtil {
    private static final String INIT_DB_SQL_FILENAME = "src/main/resources/init_db.sql";
    private static final String DB_NAME = "taxi_service";

    public static void resetDbToInitialState() {
        List<String> queries = new FileReaderServiceImpl()
                .readCreateTableQueryFromFile(INIT_DB_SQL_FILENAME);
        try (Connection connection = ConnectionUtil.getResetDbConnection();
                   Statement statement = connection.createStatement()) {
            statement.addBatch("DROP SCHEMA IF EXISTS " + DB_NAME + ";");
            for (String query : queries) {
                statement.addBatch(query);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Can't reset DB to initial state", e);
        }
    }
}
