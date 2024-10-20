package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.WorkdayClient;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.telegram.mapper.WorkdayMapper;
import fern.nail.art.nailscheduler.telegram.model.PeriodType;
import fern.nail.art.nailscheduler.telegram.model.Workday;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.service.WorkdayService;
import fern.nail.art.nailscheduler.telegram.utils.OffsetUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkdayServiceImpl implements WorkdayService {
    private final WorkdayClient workdayClient;
    private final WorkdayMapper workdayMapper;
    private final OffsetUtil offsetUtil;

    @Override
    public Set<WorkdayTemplate> getTemplates() {
        return workdayClient.getDefaultWorkdays();
    }

    @Override
    public Set<WorkdayTemplate> setTemplates(
            LocalTime startTime, LocalTime endTime, Set<DayOfWeek> daysOfWeek
    ) {
        WorkdayTemplateRequestDto requestDto =
                new WorkdayTemplateRequestDto(startTime, endTime, daysOfWeek);
        return workdayClient.setDefaultWorkdays(requestDto);
    }

    @Override
    public Workday getWorkday(LocalDate date) {
        int offset = offsetUtil.getDayOffsetToDate(date);
        List<Workday> workdayByPeriod = workdayClient.getWorkdayByPeriod(PeriodType.DAY, offset);
        if (workdayByPeriod.isEmpty()) {
            return null;
        }
        return workdayByPeriod.getFirst();
    }

    @Override
    public List<Workday> getWorkdays(PeriodType periodType, int offset) {
        return workdayClient.getWorkdayByPeriod(periodType, offset);
    }

    @Override
    public boolean setToDefault(LocalDate date) {
        return workdayClient.setDefaultWorkday(date);
    }

    @Override
    public WorkdayTemplate getTemplateOf(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return getTemplates().stream()
                      .filter(workdayTemplate -> workdayTemplate.getDayOfWeek() == dayOfWeek)
                      .findFirst()
                      .orElseThrow(() ->
                              new IllegalArgumentException(
                                      "No workday template found for date: " + date));
    }

    @Override
    public boolean setWorkday(Workday workday) {
        return workdayClient.setWorkday(workdayMapper.toDto(workday));
    }
}
