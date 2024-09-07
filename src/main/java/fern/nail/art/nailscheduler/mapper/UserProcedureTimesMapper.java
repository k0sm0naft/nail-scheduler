package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserProcedureTimesMapper {
    default Set<ProcedureTimeDto> toDtos(Set<UserProcedureTime> procedureTimes) {
        return procedureTimes.stream()
                             .map(pt -> new ProcedureTimeDto(pt.getId().getProcedure(),
                                     pt.getDuration()))
                             .collect(Collectors.toSet());
    }
}
