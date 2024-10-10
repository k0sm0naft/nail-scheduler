package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.common.annotation.Password;
import fern.nail.art.nailscheduler.common.annotation.Username;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank
        @Username
        String username,

        @NotBlank
        @Password
        String password
) {
}
