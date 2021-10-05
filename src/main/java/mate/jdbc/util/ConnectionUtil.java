package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    public static final String JDBC_DRIVER_PATH = "com.mysql.cj.jdbc.Driver";
    public static final String JDBC_CONNECTION_PATH = "jdbc:mysql://localhost:3306/taxi";
    public static final String PASSWORD = "bvf5u8";
    public static final String LOGIN = "root";

    static {
        try {
            Class.forName(JDBC_DRIVER_PATH);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't create DB drive for mySql" + JDBC_DRIVER_PATH, e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties dbProperties = new Properties();
            dbProperties.put("user", LOGIN);
            dbProperties.put("password", PASSWORD);
            return DriverManager
                    .getConnection(JDBC_CONNECTION_PATH, dbProperties);
        } catch (SQLException throwables) {
            throw new RuntimeException("Can't "
                    + "create connection to DB in DriverManager", throwables);
        }
    }
}
