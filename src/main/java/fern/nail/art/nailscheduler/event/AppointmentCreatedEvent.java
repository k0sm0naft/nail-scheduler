package fern.nail.art.nailscheduler.event;

import fern.nail.art.nailscheduler.model.Appointment;

public record AppointmentCreatedEvent(Appointment appointment) {
}
