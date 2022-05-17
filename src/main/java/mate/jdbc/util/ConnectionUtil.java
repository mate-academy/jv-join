package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static final String CONNECTION_ADDRESS = "jdbc:mysql://localhost:3306/taxi_app_db";
    private static final String USER = "root";
    private static final String PASSWORD = "gnrPswrd";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find SQL Driver", e);
        }
    }

    public static Connection getConnection() {
        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", USER);
        dbProperties.setProperty("password", PASSWORD);
        try {
            return DriverManager.getConnection(CONNECTION_ADDRESS, dbProperties);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to DB ", e);
        }
    }
}
