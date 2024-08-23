package fern.nail.art.nailscheduler.exception;

import fern.nail.art.nailscheduler.model.Appointment;

public class AppointmentStatusException extends RuntimeException {
    public AppointmentStatusException(Appointment.Status status) {
        super(status.name().toLowerCase());
    }
}
