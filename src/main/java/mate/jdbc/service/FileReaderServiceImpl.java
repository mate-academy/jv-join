package mate.jdbc.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReaderServiceImpl implements FileReaderService {
    private static final String FIRST_LINE_START_OF_CREATE_TABLE_QUERY = "DROP TABLE IF EXISTS";

    @Override
    public String readCreateTableQueryFromFile(String fileName) {
        List<String> allLines;
        try {
            allLines = Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from file: " + fileName);
        }
        List<String> linesNeedToRemove = new ArrayList<>();
        for (String line : allLines) {
            if (line.contains(FIRST_LINE_START_OF_CREATE_TABLE_QUERY)) {
                break;
            }
            linesNeedToRemove.add(line);
        }
        allLines.removeAll(linesNeedToRemove);
        return String.join(System.lineSeparator(), allLines);
    }
}
