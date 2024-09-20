package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.slot.MasterBusySlotResponseDto;
import fern.nail.art.nailscheduler.dto.slot.MasterFreeSlotResponseDto;
import fern.nail.art.nailscheduler.dto.slot.PublicSlotResponseDto;
import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = UserProcedureTimesMapper.class)
public interface SlotMapper {
    @Mapping(target = "isAvailable", expression = "java(slot.getAppointment() == null)")
    PublicSlotResponseDto toPublicDto(Slot slot);

    @Mapping(target = "procedureTime", source = "appointment.userProcedureTime")
    @Mapping(target = "appointmentStatus", source = "appointment.status")
    @Mapping(target = "slotStatus", source = "status")
    @Mapping(target = "notes", source = "appointment.notes")
    @Mapping(target = "appointmentCreatedAt", source = "appointment.createdAt")
    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "userId", source = "appointment.userProcedureTime.user.id")
    MasterBusySlotResponseDto toBusyMasterDto(Slot slot);

    @Mapping(target = "slotStatus", source = "status")
    MasterFreeSlotResponseDto toFreeMasterDto(Slot slot);

    @Mapping(target = "status", source = "requestDto.isPublished", qualifiedByName = "setStatus")
    Slot toModel(SlotRequestDto requestDto, Long id);

    @Named("setStatus")
    default Slot.Status setStatus(Boolean isPublished) {
        return isPublished ? Slot.Status.PUBLISHED : Slot.Status.UNPUBLISHED;
    }
}
