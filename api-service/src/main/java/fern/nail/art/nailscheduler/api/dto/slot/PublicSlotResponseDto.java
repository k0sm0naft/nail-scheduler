package fern.nail.art.nailscheduler.api.dto.slot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PublicSlotResponseDto extends SlotResponseDto {
    private Boolean isAvailable;
}
