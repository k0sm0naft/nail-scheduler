package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {SlotMapper.class})
public interface AppointmentMapper {
    AppointmentResponseDto toDto(Appointment appointment);

    @Mapping(source = "slotId", target = "slot", qualifiedByName = "slotById")
    Appointment toModel(AppointmentRequestDto requestDto);

    @Named(value = "slotById")
    default Slot slotById(Long slotId) {
        Slot slot = new Slot();
        slot.setId(slotId);
        return slot;
    }
}
