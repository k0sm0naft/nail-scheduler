package fern.nail.art.nailscheduler.api.dto.appointment;

import fern.nail.art.nailscheduler.api.model.ProcedureType;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDto(
        @NotNull
        Long slotId,

        @NotNull
        ProcedureType procedure,

        String notes
) {
}
