package hr.tvz.vibecheck.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {super("User not found!");}
}
