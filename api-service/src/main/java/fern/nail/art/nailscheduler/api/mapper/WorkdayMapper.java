package fern.nail.art.nailscheduler.api.mapper;

import fern.nail.art.nailscheduler.api.config.MapperConfig;
import fern.nail.art.nailscheduler.api.dto.workday.WorkdayDto;
import fern.nail.art.nailscheduler.api.model.Workday;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface WorkdayMapper {
    Workday toModel(WorkdayDto workdayDto);

    WorkdayDto toDto(Workday workday);
}
