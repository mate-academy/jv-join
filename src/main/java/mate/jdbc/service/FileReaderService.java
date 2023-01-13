package mate.jdbc.service;

import java.util.List;

public interface FileReaderService {
    List<String> readCreateTableQueryFromFile(String fileName);
}
