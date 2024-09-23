package fern.nail.art.nailscheduler.telegram.exception;

public class SendMessageException extends RuntimeException {
    public SendMessageException(String message, Exception e) {
        super(message, e);
    }
}
