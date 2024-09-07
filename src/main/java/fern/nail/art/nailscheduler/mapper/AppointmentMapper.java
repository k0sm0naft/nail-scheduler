package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.dto.slot.PublicSlotResponseDto;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = UserProcedureTimesMapper.class)
public interface AppointmentMapper {
    @Mapping(target = "slot", source = "slot", qualifiedByName = "toSlotDto")
    AppointmentResponseDto toDto(Appointment appointment);

    @Named("toSlotDto")
    default PublicSlotResponseDto toSlotDto(Slot slot) {
        PublicSlotResponseDto dto = new PublicSlotResponseDto();
        dto.setId(slot.getId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setIsAvailable(slot.getAppointment() != null);
        return dto;
    }

    @Mapping(source = "slotId", target = "slot", qualifiedByName = "slotById")
    Appointment toModel(AppointmentRequestDto requestDto);

    @Named(value = "slotById")
    default Slot slotById(Long slotId) {
        Slot slot = new Slot();
        slot.setId(slotId);
        return slot;
    }
}
