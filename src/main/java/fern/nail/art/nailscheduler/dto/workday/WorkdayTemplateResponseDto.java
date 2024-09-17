package fern.nail.art.nailscheduler.dto.workday;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkdayTemplateResponseDto(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
