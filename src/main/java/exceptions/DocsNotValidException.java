package exceptions;

public class DocsNotValidException extends RuntimeException {
    public DocsNotValidException(String message) {
        super(message);
    }
}
