package fern.nail.art.nailscheduler.api.listener;

import fern.nail.art.nailscheduler.api.event.AppointmentCreatedEvent;
import fern.nail.art.nailscheduler.api.service.WeekendManager;
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
