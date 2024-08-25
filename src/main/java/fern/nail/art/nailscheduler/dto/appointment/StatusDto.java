package fern.nail.art.nailscheduler.dto.appointment;

import jakarta.validation.constraints.NotNull;

public record StatusDto(
        @NotNull
        Boolean isConfirmed
) {
}
