package fern.nail.art.nailscheduler.api.dto.slot;

import fern.nail.art.nailscheduler.api.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.api.model.Appointment;
import fern.nail.art.nailscheduler.api.model.Slot;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MasterBusySlotResponseDto extends SlotResponseDto {
    private Long userId;

    private ProcedureTimeDto procedureTime;

    private Long appointmentId;

    private Appointment.Status appointmentStatus;

    private LocalDateTime appointmentCreatedAt;

    private String notes;

    private Slot.Status slotStatus;
}

