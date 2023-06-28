package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/taxi_service";
    private static final String USERNAME = "taxi_service";
    private static final String PASSWORD = "jXmXIUZ@t66.cq7U";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final Properties dbProperties = new Properties();

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find SQL Driver", e);
        }
        dbProperties.setProperty("user", USERNAME);
        dbProperties.setProperty("password", PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, dbProperties);
    }
}
