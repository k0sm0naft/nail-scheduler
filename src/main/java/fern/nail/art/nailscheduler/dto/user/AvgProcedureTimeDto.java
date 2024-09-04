package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.model.ProcedureType;
import jakarta.validation.constraints.NotNull;

public record AvgProcedureTimeDto(
        @NotNull
        ProcedureType procedure,
        @NotNull
        Integer time
) {
}
