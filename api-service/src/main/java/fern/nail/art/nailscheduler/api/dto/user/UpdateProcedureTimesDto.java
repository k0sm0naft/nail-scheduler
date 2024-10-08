package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.api.annotation.ValidEnumSize;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UpdateProcedureTimesDto(
        @NotNull
        @Valid
        @ValidEnumSize(enumClass = ProcedureType.class, message = "{validation.numbers.of.items}")
        Set<ProcedureTimeDto> procedureTimes
) {
}
