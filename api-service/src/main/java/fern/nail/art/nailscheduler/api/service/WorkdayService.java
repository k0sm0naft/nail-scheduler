package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.Range;
import fern.nail.art.nailscheduler.api.model.Workday;
import java.time.LocalDate;
import java.util.List;

public interface WorkdayService {
    Workday createOrUpdate(Workday workday);

    List<Workday> getByPeriod(PeriodType period, int offset);

    List<Workday> getByRange(Range range);

    void delete(LocalDate date);
}
