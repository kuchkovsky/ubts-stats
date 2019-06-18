package ua.org.ubts.stats.exception;

public class UserNotFoundException extends DatabaseItemNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
