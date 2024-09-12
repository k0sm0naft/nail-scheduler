package fern.nail.art.nailscheduler.job;

import fern.nail.art.nailscheduler.service.SlotService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotScheduledJob {
    private final SlotService service;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanOldFreeSlots() {
        service.deleteEmptyBefore(LocalDate.now());
    }
}
