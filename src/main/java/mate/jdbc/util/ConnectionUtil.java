package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    public static final String DRIVER_NAME = "org.mariadb.jdbc.Driver";
    public static final String URL = "jdbc:mariadb://192.168.1.40:3306/taxi_service_db";
    public static final String USER = "user";
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_NAME = "MariaDB#10";

    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load JDBC driver for MariaDB", e);
        }
    }

    public static Connection getConnect() {
        try {
            Properties dbProperties = new Properties();
            dbProperties.put(USER, USER_NAME);
            dbProperties.put(PASSWORD, PASSWORD_NAME);
            return DriverManager.getConnection(URL, dbProperties);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to DB", e);
        }
    }
}
