package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static final String URL = "taxi_service";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1984";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find SQL Driver", e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties dbProperties = new Properties();
            dbProperties.setProperty("user", USERNAME);
            dbProperties.setProperty("password", PASSWORD);
            return DriverManager.getConnection("jdbc:mysql:"
                    + "//localhost:3306/taxi_service", dbProperties);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to Db: taxi_service ", e);
        }
    }
}
