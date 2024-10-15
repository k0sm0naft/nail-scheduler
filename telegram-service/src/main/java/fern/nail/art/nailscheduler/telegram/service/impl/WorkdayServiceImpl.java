package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.WorkdayClient;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.service.WorkdayService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkdayServiceImpl implements WorkdayService {
    private final WorkdayClient workdayClient;

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
}
