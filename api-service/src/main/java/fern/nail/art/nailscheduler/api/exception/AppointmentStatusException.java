package fern.nail.art.nailscheduler.api.exception;

import fern.nail.art.nailscheduler.api.model.Appointment;

public class AppointmentStatusException extends RuntimeException {
    public AppointmentStatusException(Appointment.Status status) {
        super(status.name().toLowerCase());
    }
}
