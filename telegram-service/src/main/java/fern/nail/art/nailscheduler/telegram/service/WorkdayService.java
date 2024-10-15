package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public interface WorkdayService {
    Set<WorkdayTemplate> getTemplates();

    Set<WorkdayTemplate> setTemplates(
            LocalTime startTime, LocalTime endTime, Set<DayOfWeek> daysOfWeek
    );
}
