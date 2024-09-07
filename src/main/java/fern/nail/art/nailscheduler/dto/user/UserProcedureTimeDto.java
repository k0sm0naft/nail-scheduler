package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.model.ProcedureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserProcedureTimeDto(
        @NotNull
        ProcedureType procedure,

        @NotNull
        @Min(value = 30, message = "${validation.short.duration}")
        Integer duration
) {
}
