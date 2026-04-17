package hr.tvz.vibecheck.exception.custom;

public class DuplicateFollowRequestException extends RuntimeException {
    public DuplicateFollowRequestException(String message) {
        super(message);
    }
}
