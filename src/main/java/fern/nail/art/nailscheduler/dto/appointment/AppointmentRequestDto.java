package fern.nail.art.nailscheduler.dto.appointment;

import fern.nail.art.nailscheduler.model.ProcedureType;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDto(
        @NotNull
        Long slotId,

        @NotNull
        ProcedureType procedure,

        String notes
) {
}
