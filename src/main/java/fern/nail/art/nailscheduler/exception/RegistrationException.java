package fern.nail.art.nailscheduler.exception;

public class RegistrationException extends Exception {
    public RegistrationException(String username) {
        super(username);
    }
}
