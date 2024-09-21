package fern.nail.art.nailscheduler.api.exception;

import fern.nail.art.nailscheduler.api.model.Slot;

public class SlotConflictException extends RuntimeException {
    public SlotConflictException(Slot slot) {
        super("%s: %s".formatted(slot.getDate(), slot.getStartTime()));
    }
}
