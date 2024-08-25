package fern.nail.art.nailscheduler.exception;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String username) {
        super(username);
    }
}
