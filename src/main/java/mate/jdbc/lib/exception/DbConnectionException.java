package mate.jdbc.lib.exception;

public class DbConnectionException extends RuntimeException {
    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
