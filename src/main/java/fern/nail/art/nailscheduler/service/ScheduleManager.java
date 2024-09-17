package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import java.util.List;

public interface ScheduleManager {
    List<Slot> getModifiedSlots(List<Slot> originalSlots, int duration, Range range);
}
