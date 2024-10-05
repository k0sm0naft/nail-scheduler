package fern.nail.art.nailscheduler.telegram.exception;

public class ProcessorNotFoundException extends RuntimeException {
    public ProcessorNotFoundException(String message) {
        super(message);
    }
}
