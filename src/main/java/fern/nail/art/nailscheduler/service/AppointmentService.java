package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.User;
import java.util.List;

public interface AppointmentService {
    AppointmentResponseDto create(AppointmentRequestDto requestDto, User user);

    AppointmentResponseDto changeStatus(Long appointmentId, boolean isConfirmed, User user);

    AppointmentResponseDto get(Long appointmentId, User user);

    List<AppointmentResponseDto> getAll(User user);

    void delete(Long appointmentId, User user);

    void delete(Appointment appointment);
}
