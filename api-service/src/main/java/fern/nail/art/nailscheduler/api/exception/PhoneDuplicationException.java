package fern.nail.art.nailscheduler.api.exception;

public class PhoneDuplicationException extends RuntimeException {
    public PhoneDuplicationException(String phone) {
        super(phone);
    }
}
