package fern.nail.art.nailscheduler.telegram.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record WorkdayTemplateRequestDto(
        LocalTime startTime,
        LocalTime endTime,
        Set<DayOfWeek> daysOfWeek
) {
}
