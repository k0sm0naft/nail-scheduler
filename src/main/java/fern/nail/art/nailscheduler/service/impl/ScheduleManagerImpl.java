package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.Workday;
import fern.nail.art.nailscheduler.service.ScheduleManager;
import fern.nail.art.nailscheduler.service.WorkdayService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleManagerImpl implements ScheduleManager {
    private final WorkdayService workdayService;

    @Override
    public List<Slot> getModifiedSlots(List<Slot> originalSlots, int duration, Range range) {
        Map<LocalDate, Workday> workdayMap =
                workdayService.getByRange(range).stream().collect(Collectors.toMap(Workday::getDate,
                        Function.identity()));
        return originalSlots.stream()
                            .sorted(Slot::compareTo)
                            .collect(Collectors.groupingBy(Slot::getDate))
                            .values().parallelStream()
                            .map(daySlots -> getProcessedSlots(duration, daySlots,
                                    getWorkday(daySlots, workdayMap)))
                            .flatMap(Collection::stream)
                            .toList();
    }

    private List<Slot> getProcessedSlots(int duration, List<Slot> slots, Workday workday) {
        boolean isDayFromPast = slots.getFirst().getDate().isBefore(LocalDate.now());

        if (isDayFromPast) {
            return slots;
        }

        DayProcessor dayProcessor = new DayProcessor(slots, duration);
        return dayProcessor.process().stream()
                            .filter(slot -> isWithinWorkingHours(slot, workday, duration))
                            .toList();
    }

    private Workday getWorkday(List<Slot> daySlots, Map<LocalDate, Workday> workdayMap) {
        return workdayMap.get(daySlots.getFirst().getDate());
    }

    private boolean isWithinWorkingHours(Slot slot, Workday workday, int duration) {
        boolean isAfterWorkStart = !slot.getStartTime().isBefore(workday.getStartTime());
        boolean isBeforeWorkEnd =
                !slot.getStartTime().plusMinutes(duration).isAfter(workday.getEndTime());

        return isAfterWorkStart && isBeforeWorkEnd;
    }

    @RequiredArgsConstructor
    private class DayProcessor {
        private final List<Slot> slots;
        private final int duration;
        private final List<Slot> modifiedSlots = new ArrayList<>();
        private int currentIndex;
        private Slot prevBookedSlot;
        private Slot nextBookedSlot;

        private List<Slot> process() {
            while (currentIndex < slots.size()) {
                Slot currentSlot = slots.get(currentIndex);
                nextBookedSlot = isBooked(currentSlot) ? currentSlot : getNextBookedSlot();

                if (nextBookedSlot == null) {
                    handleNoNextBookedSlot(currentSlot);
                    break;
                }

                if (nextBookedSlot.equals(currentSlot)) {
                    handleNextBookedSlot(currentSlot);
                } else {
                    handleRegularSlot(currentSlot);
                }

                currentIndex++;
            }

            handleRemainingSlots();

            return modifiedSlots;
        }

        private void handleNoNextBookedSlot(Slot currentSlot) {
            adjustSlotStartTime();

            for (int i = currentIndex + 1; i < slots.size(); i++) {
                Slot nextSlot = slots.get(i);
                LocalTime nextStartTime = nextSlot.getStartTime();

                if (currentSlot.getStartTime().isBefore(nextStartTime)) {
                    break;
                }
                currentIndex++;
            }

            modifiedSlots.add(currentSlot);
            currentIndex++;
        }

        private void handleNextBookedSlot(Slot currentSlot) {
            modifiedSlots.add(currentSlot);
            prevBookedSlot = currentSlot;
            nextBookedSlot = getNextBookedSlot();
        }

        private void handleRegularSlot(Slot currentSlot) {
            adjustSlotStartTime();

            boolean isPrevNotBooked =
                    prevBookedSlot != null && !isBooked(slots.get(currentIndex - 1));
            if (isPrevNotBooked && getEndTime(prevBookedSlot).isAfter(currentSlot.getStartTime())) {
                return;
            }

            LocalTime currentEndTime = currentSlot.getStartTime().plusMinutes(duration);

            if (currentEndTime.isBefore(nextBookedSlot.getStartTime())) {
                modifiedSlots.add(currentSlot);
            } else {
                handleInterceptionWithBookedSlots(currentSlot);
            }
        }

        private void handleInterceptionWithBookedSlots(Slot currentSlot) {
            LocalTime newStartTime = nextBookedSlot.getStartTime().minusMinutes(duration);

            if (prevBookedSlot != null && newStartTime.isBefore(getEndTime(prevBookedSlot))) {
                currentIndex = slots.indexOf(nextBookedSlot) - 1;
            } else {
                currentSlot.setStartTime(newStartTime);
                modifiedSlots.add(currentSlot);
                currentIndex = slots.indexOf(nextBookedSlot) - 1;
            }
        }

        private void handleRemainingSlots() {
            if (currentIndex < slots.size()) {
                modifiedSlots.addAll(slots.subList(currentIndex, slots.size()));
            }
        }

        private Slot getNextBookedSlot() {
            for (int i = currentIndex + 1; i < slots.size(); i++) {
                Slot slot = slots.get(i);
                if (isBooked(slot)) {
                    return slot;
                }
            }
            return null;
        }

        private void adjustSlotStartTime() {
            if (currentIndex > 0) {
                Slot previousSlot = slots.get(currentIndex - 1);

                if (isBooked(previousSlot)) {
                    Slot currentSlot = slots.get(currentIndex);
                    currentSlot.setStartTime(getEndTime(previousSlot));
                }
            }
        }

        private boolean isBooked(Slot slot) {
            return slot != null && slot.getAppointment() != null;
        }

        private LocalTime getEndTime(Slot slot) {
            Appointment appointment = slot.getAppointment();
            return slot.getStartTime()
                       .plusMinutes(appointment.getUserProcedureTime().getDuration());
        }
    }
}
