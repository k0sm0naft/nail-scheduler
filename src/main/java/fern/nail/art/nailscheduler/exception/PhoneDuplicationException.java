package fern.nail.art.nailscheduler.exception;

public class PhoneDuplicationException extends RuntimeException {
    public PhoneDuplicationException(String phone) {
        super(phone);
    }
}
