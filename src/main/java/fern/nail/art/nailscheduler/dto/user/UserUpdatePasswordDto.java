package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.annotation.PasswordMatchValidator;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatchValidator(field = "password",
        fieldMatch = "repeatPassword")
public record UserUpdatePasswordDto(
        @NotBlank
        @Length(min = 8, max = 24)
        String password,

        @NotBlank
        @Length(min = 8, max = 24)
        String repeatPassword
) {
}
