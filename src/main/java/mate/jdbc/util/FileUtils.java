package mate.jdbc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FileUtils {

    public static Properties readProperties(String filename) throws IOException {
        Properties properties = new Properties();
        properties.load(getInputStream(filename));
        return properties;
    }

    public static String readFile(String filename) throws IOException {
        InputStream inputStream = getInputStream(filename);
        return readFromInputStream(inputStream);
    }

    private static InputStream getInputStream(String filename) throws IOException {
        InputStream resourceAsStream = FileUtils.class.getResourceAsStream(filename);
        if (resourceAsStream == null) {
            throw new IOException("File " + filename + " not found!");
        }
        return resourceAsStream;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append(System.lineSeparator());
            }
        }
        return resultStringBuilder.toString();
    }
}
