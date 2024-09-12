package fern.nail.art.nailscheduler.dto.user;

import java.util.Set;

public record UserFullResponseDto(
        Long id,
        String username,
        String phone,
        String firstName,
        String lastName,
        Set<ProcedureTimeDto> procedureTimes
) {
}
