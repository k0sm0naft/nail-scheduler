package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.annotation.ValidEnumSize;
import fern.nail.art.nailscheduler.model.ProcedureType;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UpdateAvgProcedureTimesDto(
        @NotNull
        @ValidEnumSize(enumClass = ProcedureType.class, message = "{validation.numbers.of.items}")
        Set<AvgProcedureTimeDto> avgProcedureTimes
) {
}
