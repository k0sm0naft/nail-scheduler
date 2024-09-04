package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.user.AvgProcedureTimeDto;
import fern.nail.art.nailscheduler.model.ProcedureType;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AvgProcedureTimesMapper {
    default Set<AvgProcedureTimeDto> toDto(Map<ProcedureType, Integer> map) {
        return map.entrySet().stream()
                  .map(e -> new AvgProcedureTimeDto(e.getKey(), e.getValue()))
                  .collect(Collectors.toSet());
    }

    default Map<ProcedureType, Integer> toMap(Set<AvgProcedureTimeDto> dtoList) {
        return dtoList.stream()
                      .collect(Collectors.toMap(
                              AvgProcedureTimeDto::procedure, AvgProcedureTimeDto::time));
    }
}
