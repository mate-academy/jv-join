package mate.jdbc.exception;

public class UniqueRecordWasDeletedException extends RuntimeException {
    public UniqueRecordWasDeletedException(String message) {
        super(message);
    }
}
