package hr.tvz.vibecheck.exception.custom;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {super("User not found!");}
}
