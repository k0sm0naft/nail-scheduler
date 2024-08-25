package fern.nail.art.nailscheduler.dto.appointment;

import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,

        SlotResponseDto slot,

        Long clientId,

        String notes,

        String status,

        LocalDateTime createdAt
) {
}
