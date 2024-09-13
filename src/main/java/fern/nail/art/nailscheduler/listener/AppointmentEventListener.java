package fern.nail.art.nailscheduler.listener;

import fern.nail.art.nailscheduler.event.AppointmentCreatedEvent;
import fern.nail.art.nailscheduler.service.WeekendManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventListener {
    private final WeekendManager weekendManager;

    @EventListener
    public void handleAppointmentBookedEvent(AppointmentCreatedEvent event) {
        weekendManager.processWeekByDate(event.appointment().getSlot().getDate());
    }
}
