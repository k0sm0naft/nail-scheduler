package fern.nail.art.nailscheduler.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Length(min = 5, max = 24)
        String username,

        @NotBlank
        @Length(min = 8, max = 24)
        String password
) {
}
