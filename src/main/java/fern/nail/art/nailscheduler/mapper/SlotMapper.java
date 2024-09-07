package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.slot.MasterSlotResponseDto;
import fern.nail.art.nailscheduler.dto.slot.PublicSlotResponseDto;
import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = AppointmentMapper.class)
public interface SlotMapper {
    @Mapping(target = "isAvailable", expression = "java(slot.getAppointment() == null)")
    PublicSlotResponseDto toPublicDto(Slot slot);

    MasterSlotResponseDto toMasterDto(Slot slot);

    @Mapping(target = "status", source = "isPublished", qualifiedByName = "setStatus")
    Slot toModel(SlotRequestDto requestDto);

    @Named("setStatus")
    default Slot.Status setStatus(Boolean isPublished) {
        return isPublished ? Slot.Status.PUBLISHED : Slot.Status.UNPUBLISHED;
    }
}
