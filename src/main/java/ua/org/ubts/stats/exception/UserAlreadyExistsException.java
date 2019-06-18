package ua.org.ubts.stats.exception;

public class UserAlreadyExistsException extends ConflictException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
