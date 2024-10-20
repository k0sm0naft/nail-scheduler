package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.PeriodType;
import fern.nail.art.nailscheduler.telegram.model.Workday;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface WorkdayService {
    Set<WorkdayTemplate> getTemplates();

    Set<WorkdayTemplate> setTemplates(
            LocalTime startTime, LocalTime endTime, Set<DayOfWeek> daysOfWeek
    );

    Workday getWorkday(LocalDate date);

    List<Workday> getWorkdays(PeriodType periodType, int offset);

    boolean setToDefault(LocalDate date);

    WorkdayTemplate getTemplateOf(LocalDate date);

    boolean setWorkday(Workday workday);
}
