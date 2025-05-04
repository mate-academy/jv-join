package mate.jdbc.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileReaderServiceImpl implements FileReaderService {
    @Override
    public List<String> readCreateTableQueryFromFile(String fileName) {
        List<String> allLines;
        try {
            allLines = Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from file: " + fileName);
        }
        return Arrays.stream(String.join("", allLines)
                .split(";"))
                .map(s -> s += ";")
                .collect(Collectors.toList());
    }
}
