package mate.jdbc;

import java.sql.Connection;
import mate.jdbc.util.ConnectionUtil;

public class Main {
    public static void main(String[] args) {
        Connection connection = ConnectionUtil.getConnection();
    }
}
