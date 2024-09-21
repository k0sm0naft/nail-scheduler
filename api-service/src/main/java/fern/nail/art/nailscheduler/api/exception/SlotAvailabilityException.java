package fern.nail.art.nailscheduler.api.exception;

import fern.nail.art.nailscheduler.api.model.Slot;

public class SlotAvailabilityException extends SlotConflictException {
    public SlotAvailabilityException(Slot slot) {
        super(slot);
    }
}
