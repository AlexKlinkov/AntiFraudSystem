package antifraud.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("Conflict");
    }
}
