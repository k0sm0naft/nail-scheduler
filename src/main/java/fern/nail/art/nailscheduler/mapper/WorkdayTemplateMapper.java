package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.workday.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.dto.workday.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.model.WorkdayTemplate;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface WorkdayTemplateMapper {
    WorkdayTemplateResponseDto toDto(WorkdayTemplate templates);

    WorkdayTemplate toModel(WorkdayTemplateRequestDto templateDto);
}
