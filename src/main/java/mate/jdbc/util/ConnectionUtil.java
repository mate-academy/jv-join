package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import mate.jdbc.lib.exception.DataProcessingException;

public class ConnectionUtil {
    public static final String URL = "jdbc:mysql://localhost:3306/manufacturers";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "3510";
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new DataProcessingException("Can`t load JDBC driver for MySQL: "
                    + JDBC_DRIVER, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection;
        try {
            Properties dbProperties = new Properties();
            dbProperties.put("user", USERNAME);
            dbProperties.put("password", PASSWORD);
            connection =
                    DriverManager.getConnection(URL,
                            dbProperties);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create connection to DB " + URL
                    + " for user: " + USERNAME, e);
        }
        return connection;
    }
}
