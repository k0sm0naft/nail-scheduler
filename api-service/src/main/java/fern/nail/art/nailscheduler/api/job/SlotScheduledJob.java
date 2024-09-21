package fern.nail.art.nailscheduler.api.job;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.Slot;
import fern.nail.art.nailscheduler.api.service.SlotService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotScheduledJob {
    private final SlotService service;

    //todo move to service
    @EventListener(ApplicationReadyEvent.class)
    private void onApplicationReady() {
        cleanOldFreeSlots();
        generateSlotsForTwoMonthAhead();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanOldFreeSlots() {
        service.deleteEmptyBefore(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateSlotsOnDayAfterTwoMonths() {
        service.generateSlotsForDay(LocalDate.now().plusMonths(2));
    }

    private void generateSlotsForTwoMonthAhead() {
        LocalDate endDate = LocalDate.now().plusMonths(2).plusDays(1);
        Stream.iterate(getStartDate(), endDate::isAfter, day -> day.plusDays(1))
                .forEach(service::generateSlotsForDay);
    }

    private LocalDate getStartDate() {
        int maxOffset = 2;
        List<Slot> slots = new ArrayList<>();
        for (int i = maxOffset; i >= 0; i--) {
            slots = service.getAllByPeriod(PeriodType.MONTH, i);
            if (!slots.isEmpty()) {
                break;
            }
        }
        return slots.stream()
                    .max(Slot::compareTo)
                    .map(slot -> slot.getDate().plusDays(1))
                    .orElseGet(LocalDate::now);
    }
}
