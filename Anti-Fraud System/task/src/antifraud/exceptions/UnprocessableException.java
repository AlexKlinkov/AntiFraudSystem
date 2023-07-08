package antifraud.exceptions;

public class UnprocessableException extends RuntimeException{
    public UnprocessableException() {
        super("422_Unprocessable");
    }
}
