package fern.nail.art.nailscheduler.api.event;

import fern.nail.art.nailscheduler.api.model.Appointment;

public record AppointmentCreatedEvent(Appointment appointment) {
}
