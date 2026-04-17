package hr.tvz.vibecheck.exception;

public class NotYourRequestException extends RuntimeException {
    public NotYourRequestException(String message) {
        super(message);
    }
}
