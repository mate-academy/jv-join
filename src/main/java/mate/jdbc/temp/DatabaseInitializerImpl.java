package mate.jdbc.temp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;
import mate.jdbc.lib.Service;
import mate.jdbc.util.ConnectionUtil;

@Service
public class DatabaseInitializerImpl implements DatabaseInitializer {
    @Override
    public String[] readFromFile(String filePath) {
        try {
            String collect = Files.lines(Path.of(filePath))
                    .skip(1L)
                    .collect(Collectors.joining(System.lineSeparator()));
            return collect.split(";");
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from file: "
                    + filePath);
        }
    }

    @Override
    public void initializeDb(String[] queries) {
        for (String query : queries) {
            if (!query.trim().isEmpty()) {
                try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement initStatement =
                                connection.prepareStatement(query)) {
                    initStatement.execute();
                } catch (SQLException e) {
                    throw new RuntimeException("Can't initialize db with query: " + query);
                }
            }
        }
    }
}
