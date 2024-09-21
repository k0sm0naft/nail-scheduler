package fern.nail.art.nailscheduler.api.mapper;

import fern.nail.art.nailscheduler.api.config.MapperConfig;
import fern.nail.art.nailscheduler.api.dto.workday.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.api.dto.workday.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.api.model.WorkdayTemplate;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface WorkdayTemplateMapper {
    WorkdayTemplateResponseDto toDto(WorkdayTemplate templates);

    WorkdayTemplate toModel(WorkdayTemplateRequestDto templateDto);
}
