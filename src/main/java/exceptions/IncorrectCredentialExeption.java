package exceptions;

public class IncorrectCredentialExeption extends RuntimeException{
    public IncorrectCredentialExeption(String message) {
        super(message);
    }
}
