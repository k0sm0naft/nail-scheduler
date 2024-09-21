package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.model.Range;
import fern.nail.art.nailscheduler.api.model.Slot;
import java.util.List;

public interface ScheduleManager {
    List<Slot> getModifiedSlots(List<Slot> originalSlots, int duration, Range range);
}
