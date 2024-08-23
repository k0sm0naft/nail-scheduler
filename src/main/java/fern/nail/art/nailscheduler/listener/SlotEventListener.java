package fern.nail.art.nailscheduler.listener;

import fern.nail.art.nailscheduler.event.SlotDeletedEvent;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.repository.AppointmentRepository;
import fern.nail.art.nailscheduler.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotEventListener {
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    @EventListener
    public void handleSlotDeletedEvent(SlotDeletedEvent event) {
        appointmentRepository.findAllBySlotId(event.slotId()).stream()
                .filter(appointment -> appointment.getStatus() != Appointment.Status.CANCELED)
                .forEach(a -> appointmentService.changeStatus(a.getId(), false, event.user()));
    }
}