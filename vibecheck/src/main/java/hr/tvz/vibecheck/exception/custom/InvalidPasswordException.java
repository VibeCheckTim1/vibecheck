package hr.tvz.vibecheck.exception.custom;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Old password is incorrect");
    }
}
