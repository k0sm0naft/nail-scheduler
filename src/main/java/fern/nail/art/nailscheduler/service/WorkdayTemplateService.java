package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.WorkdayTemplate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

public interface WorkdayTemplateService {
    List<WorkdayTemplate> update(Set<DayOfWeek> days, WorkdayTemplate template);

    List<WorkdayTemplate> getAll();
}
