package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.workday.WorkdayDto;
import fern.nail.art.nailscheduler.model.Workday;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface WorkdayMapper {
    Workday toModel(WorkdayDto workdayDto);

    WorkdayDto toDto(Workday workday);
}
