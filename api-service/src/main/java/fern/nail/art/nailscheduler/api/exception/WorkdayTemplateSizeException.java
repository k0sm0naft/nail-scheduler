package fern.nail.art.nailscheduler.api.exception;

public class WorkdayTemplateSizeException extends RuntimeException {
    public WorkdayTemplateSizeException(int size) {
        super(String.valueOf(size));
    }
}
