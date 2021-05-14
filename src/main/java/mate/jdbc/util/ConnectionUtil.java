package mate.jdbc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import mate.jdbc.lib.exception.DbConnectionException;
import mate.jdbc.lib.exception.DriverLoadingException;
import mate.jdbc.lib.exception.PropertyFileReadingException;

public class ConnectionUtil {
    private static final Properties MYSQL_PROPERTIES = new Properties();
    private static final String DRIVER_CLASS = "driver";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String URL = "url";
    private static final String MYSQL_PROPERTIES_FILE =
            "src/main/resources/mysql_config.properties";

    static {
        try {
            MYSQL_PROPERTIES.load(new FileInputStream(MYSQL_PROPERTIES_FILE));
            Class.forName(MYSQL_PROPERTIES.getProperty(DRIVER_CLASS));
        } catch (ClassNotFoundException e) {
            throw new DriverLoadingException("Can't load JDBC driver for MySQL. Driver class: "
                    + MYSQL_PROPERTIES.getProperty(DRIVER_CLASS), e);
        } catch (IOException e) {
            throw new PropertyFileReadingException("Can't read properties file. File: "
                    + MYSQL_PROPERTIES_FILE, e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(MYSQL_PROPERTIES.getProperty(URL),
                    MYSQL_PROPERTIES.getProperty(USER),
                    MYSQL_PROPERTIES.getProperty(PASSWORD)
            );
        } catch (SQLException e) {
            throw new DbConnectionException("Can't create connection to Db with User: "
                    + MYSQL_PROPERTIES.getProperty(USER)
                    + ", URL: " + MYSQL_PROPERTIES.getProperty(URL), e);
        }
    }
}
