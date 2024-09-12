package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.service.SlotShiftingManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class SlotShiftingManagerImpl implements SlotShiftingManager {
    @Override
    public List<Slot> getModifiedSlots(List<Slot> originalSlots, int duration) {
        return originalSlots.stream()
                            .sorted(Slot::compareTo)
                            .collect(Collectors.groupingBy(Slot::getDate))
                            .values().parallelStream()
                            .map(daySlots -> getProcessedSlots(duration, daySlots))
                            .flatMap(Collection::stream)
                            .toList();
    }

    private List<Slot> getProcessedSlots(int duration, List<Slot> slots) {
        boolean isDayFromPast = slots.getFirst().getDate().isBefore(LocalDate.now());
        if (isDayFromPast) {
            return slots;
        }
        return new SlotProcessor(slots, duration).process().stream()
                                                 .filter(this::isWithinWorkingHours)
                                                 .toList();
    }

    private boolean isWithinWorkingHours(Slot slot) {
        //todo make working hours variables and set them from properties
        return slot.getStartTime().isAfter(LocalTime.of(6, 59))
                && slot.getStartTime().isBefore(LocalTime.of(19, 1));
    }

    @RequiredArgsConstructor
    private class SlotProcessor {
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

            handleLastModifiedSlot();

            return modifiedSlots;
        }

        private void handleNoNextBookedSlot(Slot currentSlot) {
            adjustSlotStartTime();

            boolean nextExist = currentIndex + 1 < slots.size();
            LocalTime nextStartTime = nextExist ? slots.get(currentIndex + 1).getStartTime() : null;
            if (nextExist && !currentSlot.getStartTime().isBefore(nextStartTime)) {
                return;
            }

            modifiedSlots.addAll(slots.subList(currentIndex, slots.size()));
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

        private void handleLastModifiedSlot() {
            if (currentIndex < slots.size()) {
                Slot lastSlot = slots.get(currentIndex);
                if (!modifiedSlots.contains(lastSlot)) {
                    modifiedSlots.add(lastSlot);
                }
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
