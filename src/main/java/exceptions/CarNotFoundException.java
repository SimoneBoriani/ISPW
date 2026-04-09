package exceptions;

public class CarNotFoundException extends RuntimeException{

    public CarNotFoundException(String s){
        super(s);
    }
}
