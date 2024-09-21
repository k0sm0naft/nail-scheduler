package fern.nail.art.nailscheduler.api.dto.workday;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record WorkdayTemplateRequestDto(
        Set<DayOfWeek> daysOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
