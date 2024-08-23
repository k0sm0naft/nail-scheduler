package fern.nail.art.nailscheduler.exception;

import java.time.LocalDate;
import java.time.LocalTime;

public class SlotConflictedException extends RuntimeException {
    public SlotConflictedException(LocalDate date, LocalTime startTime, LocalTime endTime) {
        super("%s: %s - %s".formatted(date, startTime, endTime));
    }
}
