package mate.jdbc.lib.exception;

public class DriverLoadingException extends RuntimeException {
    public DriverLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
