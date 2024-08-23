package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface SlotMapper {
    SlotResponseDto toDto(Slot slot);

    Slot toModel(SlotRequestDto requestDto);
}
