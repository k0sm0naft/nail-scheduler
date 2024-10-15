package fern.nail.art.nailscheduler.telegram.mapper;

import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkdayMapper {
    public WorkdayTemplate toTemplate(WorkdayTemplateResponseDto templateDto) {
        return new WorkdayTemplate(
                templateDto.dayOfWeek(),
                templateDto.startTime(),
                templateDto.endTime()
        );
    }
}
