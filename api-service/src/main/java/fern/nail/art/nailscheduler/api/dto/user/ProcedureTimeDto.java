package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.api.model.ProcedureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProcedureTimeDto(
        @NotNull
        ProcedureType procedure,

        @NotNull
        @Min(value = 30, message = "${validation.short.duration}")
        Integer duration
) {
}
