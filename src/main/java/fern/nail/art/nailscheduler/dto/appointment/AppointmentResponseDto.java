package fern.nail.art.nailscheduler.dto.appointment;

import fern.nail.art.nailscheduler.dto.slot.PublicSlotResponseDto;
import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,

        PublicSlotResponseDto slot,

        ProcedureTimeDto userProcedureTime,

        String notes,

        String status,

        LocalDateTime createdAt
) {
}
