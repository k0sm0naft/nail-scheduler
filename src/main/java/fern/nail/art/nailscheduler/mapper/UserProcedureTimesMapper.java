package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.user.UserProcedureTimeDto;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserProcedureTimesMapper {
    default Set<UserProcedureTimeDto> toDto(Set<UserProcedureTime> procedureTimes) {
        return procedureTimes.stream()
                             .map(pt -> new UserProcedureTimeDto(pt.getId().getProcedure(),
                                     pt.getDuration()))
                             .collect(Collectors.toSet());
    }
}
