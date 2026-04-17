package hr.tvz.vibecheck.exception.custom;

public class NotYourRequestException extends RuntimeException {
    public NotYourRequestException(String message) {
        super(message);
    }
}
