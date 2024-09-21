package fern.nail.art.nailscheduler.api.mapper;

import fern.nail.art.nailscheduler.api.config.MapperConfig;
import fern.nail.art.nailscheduler.api.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.UserProcedureTime;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserProcedureTimesMapper {
    default ProcedureTimeDto toDto(UserProcedureTime procedureTimes) {
        ProcedureType procedure = procedureTimes.getId().getProcedure();
        return new ProcedureTimeDto(procedure, procedureTimes.getDuration());
    }
}
