package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.WeekendManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeekendManagerImpl implements WeekendManager {
    private static final int DAY_OFF_PER_WEEK = 2;
    private final SlotService slotService;

    @Override
    public void processWeekByDate(LocalDate date) {
        int offset = (int) ChronoUnit.WEEKS.between(LocalDate.now(), date);

        Map<LocalDate, List<Slot>> slotsByDate =
                slotService.getAllByPeriod(PeriodType.WEEK, offset).stream()
                           .collect(Collectors.groupingBy(Slot::getDate));

        List<LocalDate> freeDays = slotsByDate.entrySet().stream()
                                              .filter(entry -> areSlotsFree(entry.getValue()))
                                              .map(Map.Entry::getKey)
                                              .toList();

        if (freeDays.size() <= DAY_OFF_PER_WEEK) {
            freeDays.forEach(slotService::deleteAllByDate);
        }
    }

    private boolean areSlotsFree(List<Slot> slots) {
        return slots.stream().noneMatch(this::isSlotBooked);
    }

    private boolean isSlotBooked(Slot slot) {
        return slot.getAppointment() != null;
    }
}
