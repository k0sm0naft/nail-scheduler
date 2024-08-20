package fern.nail.art.nailscheduler.dto.slot;

import fern.nail.art.nailscheduler.annotation.RangeValidator;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@RangeValidator(start = "startTime",
        end = "endTime",
        message = "Wrong or short time range.")
public record SlotRequestDto(
        @NotNull
        LocalDate date,

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        Boolean isPublished
) {
}
