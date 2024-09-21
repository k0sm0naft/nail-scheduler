package fern.nail.art.nailscheduler.api.listener;

import fern.nail.art.nailscheduler.api.event.SlotDeletedEvent;
import fern.nail.art.nailscheduler.api.repository.AppointmentRepository;
import fern.nail.art.nailscheduler.api.service.AppointmentService;
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
        //todo change on "delete all appointments where slotId..."
        appointmentRepository.findAllBySlotId(event.slotId())
                             .forEach(appointmentService::delete);
    }
}
