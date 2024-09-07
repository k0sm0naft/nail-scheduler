package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.User;
import java.util.List;

public interface AppointmentService {
    Appointment create(Appointment appointment, ProcedureType procedure, User user);

    Appointment changeStatus(Long appointmentId, boolean isConfirmed, User user);

    Appointment get(Long appointmentId, User user);

    List<Appointment> getAll(User user);

    void delete(Long appointmentId, User user);

    void delete(Appointment appointment);
}
