package antifraud.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("BadRequest");
    }
}
