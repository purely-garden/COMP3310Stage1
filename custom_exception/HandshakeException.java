package custom_exception;

public class HandshakeException extends Exception {


    public HandshakeException(String msg) {
        super(msg);
    }

    public HandshakeException(String code, String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
