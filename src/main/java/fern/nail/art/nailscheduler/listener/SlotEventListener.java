package fern.nail.art.nailscheduler.listener;

import fern.nail.art.nailscheduler.event.SlotDeletedEvent;
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
        //todo change on "delete all appointments where slotId..."
        appointmentRepository.findAllBySlotId(event.slotId())
                             .forEach(appointmentService::delete);
    }
}
