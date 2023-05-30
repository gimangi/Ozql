package exceptions;

public class OzqlException extends RuntimeException {

    public OzqlException() {
        super();
    }

    public OzqlException(String message) {
        super(message);
    }

    public OzqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public OzqlException(Throwable cause) {
        super(cause);
    }

    public OzqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
