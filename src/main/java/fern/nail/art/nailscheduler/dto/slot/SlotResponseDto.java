package fern.nail.art.nailscheduler.dto.slot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SlotResponseDto(
        long id,

        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        Boolean isPublished,

        Boolean isAvailable,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
