package hr.tvz.vibecheck.exception.custom;

public class NotPendingRequestException extends RuntimeException {
    public NotPendingRequestException(String message) {
        super(message);
    }
}
