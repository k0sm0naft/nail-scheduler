package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.service.SlotShiftingManager;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SlotShiftingManagerImpl implements SlotShiftingManager {
    @Override
    public List<Slot> getModifiedSlots(List<Slot> originalSlots, int duration) {
        return originalSlots.stream()
                            .sorted(Slot::compareTo)
                            .collect(Collectors.groupingBy(Slot::getDate))
                            .values().parallelStream()
                            .map(daySlots -> processDaySlots(daySlots, duration))
                            .flatMap(Collection::stream)
                            .filter(this::isWithinWorkingHours)
                            .toList();
    }

    private boolean isWithinWorkingHours(Slot slot) {
        return slot.getStartTime().isAfter(LocalTime.of(6, 59))
                && slot.getStartTime().isBefore(LocalTime.of(19, 1));
    }

    private List<Slot> processDaySlots(List<Slot> slots, int duration) {
        List<Slot> modifiedSlots = new ArrayList<>();
        Slot prevBookedSlot = null;
        Slot nextBookedSlot = getNextBookedSlot(slots, 0);

        for (int i = 0; i < slots.size(); i++) {
            Slot currentSlot = slots.get(i);

            // Если нет следующего забронированного слота
            if (nextBookedSlot == null) {
                adjustSlotStartTime(slots, i);

                LocalTime nextStartTime =
                        (i + 1 < slots.size()) ? slots.get(i + 1).getStartTime() : null;

                if (nextStartTime != null && !currentSlot.getStartTime().isBefore(nextStartTime)) {
                    continue;
                }

                if (prevBookedSlot != null) {
                    currentSlot.setStartTime(getEndTime(prevBookedSlot));
                }

                modifiedSlots.addAll(slots.subList(i, slots.size()));
                break;
            }

            if (nextBookedSlot.equals(currentSlot)) {
                modifiedSlots.add(currentSlot);
                prevBookedSlot = currentSlot;
                nextBookedSlot = getNextBookedSlot(slots, i + 1);
            } else {
                adjustSlotStartTime(slots, i);

                if (prevBookedSlot != null && !isBooked(slots.get(i - 1))
                        && getEndTime(prevBookedSlot).isAfter(currentSlot.getStartTime())) {
                    continue;
                }

                LocalTime currentEndTime = currentSlot.getStartTime().plusMinutes(duration);

                if (currentEndTime.isBefore(nextBookedSlot.getStartTime())) {
                    modifiedSlots.add(currentSlot);
                } else {
                    LocalTime newStartTime = nextBookedSlot.getStartTime().minusMinutes(duration);

                    if (prevBookedSlot != null
                            && newStartTime.isBefore(getEndTime(prevBookedSlot))) {
                        i = slots.indexOf(nextBookedSlot) - 1;
                    } else {
                        currentSlot.setStartTime(newStartTime);
                        modifiedSlots.add(currentSlot);
                        i = slots.indexOf(nextBookedSlot) - 1;
                    }
                }
            }
        }

        return modifiedSlots;
    }

    private Slot getNextBookedSlot(List<Slot> slots, int startIndex) {
        for (int i = startIndex; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (isBooked(slot)) {
                return slot;
            }
        }
        return null;
    }

    private void adjustSlotStartTime(List<Slot> slots, int index) {
        if (index > 0) {
            Slot previousSlot = slots.get(index - 1);
            Slot currentSlot = slots.get(index);
            if (isBooked(previousSlot)) {
                currentSlot.setStartTime(getEndTime(previousSlot));
            }
        }
    }

    private boolean isBooked(Slot slot) {
        return slot != null && slot.getAppointment() != null;
    }

    private LocalTime getEndTime(Slot slot) {
        Appointment appointment = slot.getAppointment();
        return slot.getStartTime().plusMinutes(appointment.getUserProcedureTime().getDuration());
    }
}
