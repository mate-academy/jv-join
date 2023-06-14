package mate.jdbc.temp;

public interface DatabaseInitializer {
    String[] readFromFile(String filePath);
    //I know that this is violates SOLID principles, but it's a temporary solution and will be
    // deleted in future

    void initializeDb(String[] query);
}
