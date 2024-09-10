package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.Slot;
import java.util.List;

public interface SlotShiftingManager {
    List<Slot> getModifiedSlots(List<Slot> originalSlots, int procedureDuration);
}
