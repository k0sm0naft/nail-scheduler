package fern.nail.art.nailscheduler.exception;

import fern.nail.art.nailscheduler.model.Slot;

public class SlotConflictException extends RuntimeException {
    public SlotConflictException(Slot slot) {
        super("%s: %s - %s".formatted(slot.getDate(), slot.getStartTime(), slot.getEndTime()));
    }
}
