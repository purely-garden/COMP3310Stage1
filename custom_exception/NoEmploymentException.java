package custom_exception;

public class NoEmploymentException extends Exception {

    public NoEmploymentException(String msg) {
        super(msg);
    }

    public NoEmploymentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
