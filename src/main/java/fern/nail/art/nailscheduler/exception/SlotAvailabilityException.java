package fern.nail.art.nailscheduler.exception;

import fern.nail.art.nailscheduler.model.Slot;

public class SlotAvailabilityException extends SlotConflictException {
    public SlotAvailabilityException(Slot slot) {
        super(slot);
    }
}
