package antifraud.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException() {
        super("NotFound");
    }
}
