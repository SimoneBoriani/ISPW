package exceptions;

public class NotificationErrorException extends RuntimeException {

    public NotificationErrorException(String message) {
        super(message);
    }

    public NotificationErrorException(String message, Throwable cause) {super(message, cause);}

}
