package fern.nail.art.nailscheduler.dto.slot;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotResponseDto(
        long id,

        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        Boolean isAvailable,

        Boolean isPublished
) {
}
