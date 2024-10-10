package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.common.annotation.Name;
import fern.nail.art.nailscheduler.common.annotation.Password;
import fern.nail.art.nailscheduler.common.annotation.PasswordMatchValidator;
import fern.nail.art.nailscheduler.common.annotation.Phone;
import fern.nail.art.nailscheduler.common.annotation.Username;
import jakarta.validation.constraints.NotBlank;

@PasswordMatchValidator(field = "password",
        fieldMatch = "repeatPassword")
public record UserRegistrationRequestDto(
        @Username
        @NotBlank
        String username,

        @Password
        @NotBlank
        String password,

        @Password
        @NotBlank
        String repeatPassword,

        @Phone
        @NotBlank
        String phone,

        @Name
        @NotBlank
        String firstName,

        @Name
        String lastName,

        Long telegramId
) {
}
