package fern.nail.art.nailscheduler.dto.slot;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record SlotRequestDto(
        @NotNull
        LocalDate date,

        @NotNull
        LocalTime startTime,

        @NotNull
        Boolean isPublished
) {
}
