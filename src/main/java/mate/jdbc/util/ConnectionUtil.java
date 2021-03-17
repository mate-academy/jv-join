package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find SQL Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", "root");
        dbProperties.setProperty("password", "1234");
        String url = "jdbc:mysql://localhost:3306/taxi?serverTimezone=UTC";
        return DriverManager.getConnection(url, dbProperties);
    }
}
