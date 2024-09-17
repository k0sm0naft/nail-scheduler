package fern.nail.art.nailscheduler.dto.workday;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkdayDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
