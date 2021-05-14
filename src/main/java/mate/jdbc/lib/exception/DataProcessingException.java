package mate.jdbc.lib.exception;

import java.util.function.Supplier;

public class DataProcessingException extends RuntimeException {
    public DataProcessingException(String message) {
        super(message);
    }
    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
