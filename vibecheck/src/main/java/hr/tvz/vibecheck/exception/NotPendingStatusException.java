package hr.tvz.vibecheck.exception;

public class NotPendingStatusException extends RuntimeException {
    public NotPendingStatusException(String message) {
        super(message);
    }
}
