package fern.nail.art.nailscheduler.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorDto(
        @JsonProperty("error")
        Object error
) {
}
