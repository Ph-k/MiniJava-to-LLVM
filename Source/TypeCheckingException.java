// To throw custom named exception
public class TypeCheckingException extends Exception { 
    public TypeCheckingException(String errorMessage) {
        super(errorMessage);
    }
}