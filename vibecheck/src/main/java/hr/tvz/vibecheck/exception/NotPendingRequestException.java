package hr.tvz.vibecheck.exception;

public class NotPendingRequestException extends RuntimeException {
    public NotPendingRequestException(String message) {
        super(message);
    }
}
