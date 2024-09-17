package fern.nail.art.nailscheduler.exception;

public class WorkdayTemplateSizeException extends RuntimeException {
    public WorkdayTemplateSizeException(int size) {
        super(String.valueOf(size));
    }
}
