package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = UserProcedureTimesMapper.class)
public interface AppointmentMapper {
    @Mapping(target = "slotId", source = "slot.id")
    @Mapping(target = "userId", source = "userProcedureTime.user.id")
    @Mapping(target = "date", source = "slot.date")
    @Mapping(target = "startTime", source = "slot.startTime")
    @Mapping(target = "procedureTime", source = "userProcedureTime")
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
