package fern.nail.art.nailscheduler.dto.slot;

import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.model.Slot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MasterSlotResponseDto extends SlotResponseDto {
    private AppointmentResponseDto appointment;

    private Slot.Status status;
}

