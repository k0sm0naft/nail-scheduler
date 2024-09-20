package fern.nail.art.nailscheduler.dto.appointment;

import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponseDto(
        Long id,

        Long userId,

        Long slotId,

        LocalDate date,

        LocalTime startTime,

        ProcedureTimeDto procedureTime,

        String notes,

        String status,

        LocalDateTime createdAt
) {
}
