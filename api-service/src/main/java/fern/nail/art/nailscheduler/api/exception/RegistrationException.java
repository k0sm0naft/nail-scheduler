package fern.nail.art.nailscheduler.api.exception;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String username) {
        super(username);
    }
}
