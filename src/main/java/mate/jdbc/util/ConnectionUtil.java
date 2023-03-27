package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://localhost:3306/manufacturer_db";
    private static final String USER_KEY = "user";
    private static final String USER_NAME = "root";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_PASSWORD = "121Silver121!";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can`t load jdbc driver for MySQL ", e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties properties = new Properties();
            properties.put(USER_KEY, USER_NAME);
            properties.put(USER_PASSWORD_KEY, USER_PASSWORD);
            return DriverManager.getConnection(CONNECTION, properties);
        } catch (SQLException e) {
            throw new RuntimeException("Can`t connect to db ", e);
        }
    }

}
