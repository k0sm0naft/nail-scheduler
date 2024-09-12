package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserProcedureTimesMapper {
    default ProcedureTimeDto toDto(UserProcedureTime procedureTimes) {
        ProcedureType procedure = procedureTimes.getId().getProcedure();
        return new ProcedureTimeDto(procedure, procedureTimes.getDuration());
    }
}
