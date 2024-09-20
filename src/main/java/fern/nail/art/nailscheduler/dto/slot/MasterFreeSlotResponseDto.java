package fern.nail.art.nailscheduler.dto.slot;

import fern.nail.art.nailscheduler.model.Slot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MasterFreeSlotResponseDto extends SlotResponseDto {
    private Slot.Status slotStatus;
}

