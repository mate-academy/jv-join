package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static final String URL = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "YFDKxdjw877@";
    private static final String JDBC_DRIVER = "jdbc:mysql://localhost:3306/taxi_service_db";

    static {
        try {
            Class.forName(URL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find JDBC driver", e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties dbProperties = new Properties();
            dbProperties.put("user", USERNAME);
            dbProperties.put("password", PASSWORD);
            return DriverManager.getConnection(JDBC_DRIVER,
                    dbProperties);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to DB", e);
        }
    }
}
