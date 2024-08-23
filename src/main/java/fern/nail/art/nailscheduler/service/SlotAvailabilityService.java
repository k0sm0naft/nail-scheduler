package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.Slot;

public interface SlotAvailabilityService {
    Slot changeSlotAvailability(Long slotId, boolean isAvailable);
}
