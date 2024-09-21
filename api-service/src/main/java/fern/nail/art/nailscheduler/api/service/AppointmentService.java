package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.model.Appointment;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.User;
import java.util.List;

public interface AppointmentService {
    Appointment create(Appointment appointment, ProcedureType procedure, Long userId);

    Appointment changeStatus(Long appointmentId, boolean isConfirmed, User user);

    Appointment get(Long appointmentId, User user);

    List<Appointment> getAll(Long userId);

    void delete(Long appointmentId, User user);

    void delete(Appointment appointment);
}
