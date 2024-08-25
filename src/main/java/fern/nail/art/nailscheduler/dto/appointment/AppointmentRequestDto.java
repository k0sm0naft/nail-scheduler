package fern.nail.art.nailscheduler.dto.appointment;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDto(
        @NotNull
        Long slotId,

        String notes
) {
}
