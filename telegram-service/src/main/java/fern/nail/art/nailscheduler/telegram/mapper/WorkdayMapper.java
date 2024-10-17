package fern.nail.art.nailscheduler.telegram.mapper;

import fern.nail.art.nailscheduler.telegram.dto.WorkdayDto;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.telegram.model.Workday;
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
    public Workday toWorkday(WorkdayDto dto) {
        return new Workday(
                dto.date(),
                dto.startTime(),
                dto.endTime()
        );
    }

    public WorkdayDto toDto(Workday workday) {
        return new WorkdayDto(
                workday.getDate(),
                workday.getStartTime(),
                workday.getEndTime()
        );
    }
}
