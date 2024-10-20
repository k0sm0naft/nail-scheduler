package fern.nail.art.nailscheduler.telegram.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class OffsetUtil {
    public int getMonthOffsetToDate(LocalDate date) {
        return (int) ChronoUnit.MONTHS.between(LocalDate.now(), date);
    }

    public int getWeekOffsetToDate(LocalDate date) {
        return (int) ChronoUnit.WEEKS.between(LocalDate.now(), date);
    }

    public int getDayOffsetToDate(LocalDate date) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), date);
    }
}
