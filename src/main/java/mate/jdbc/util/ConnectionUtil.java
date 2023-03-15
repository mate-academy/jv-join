package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {

    private static final String USER_KEY = "user";
    private static final String LOGIN = "root";
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD = "Mate2023";
    private static final String DB_ADDRESS = "jdbc:mysql://localhost:3306/taxi_service";
    private static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(SQL_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load JDBC driver for DBMS", e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties dbProperties = new Properties();
            dbProperties.put(USER_KEY, LOGIN);
            dbProperties.put(PASSWORD_KEY, PASSWORD);
            return DriverManager.getConnection(DB_ADDRESS, dbProperties);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to DB", e);
        }
    }
}
