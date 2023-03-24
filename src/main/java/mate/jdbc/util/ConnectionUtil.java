package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import mate.jdbc.exception.DataProcessingException;

public class ConnectionUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String USER_LOGIN = "user";
    private static final String USER_VALUE = "root";
    private static final String PASSWORD_LOGIN = "password";
    private static final String PASSWORD_VALUE = "123456";
    private static final String CONNECTION_URL
            = "jdbc:mysql://localhost:3306/taxi_service_base";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new DataProcessingException("Can't load jdbc driver", e);
        }
    }

    public static Connection getConnection() {
        Properties properties = new Properties();
        properties.put(USER_LOGIN, USER_VALUE);
        properties.put(PASSWORD_LOGIN, PASSWORD_VALUE);
        try {
            return DriverManager.getConnection(CONNECTION_URL, properties);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't connect to the database", e);
        }
    }
}
