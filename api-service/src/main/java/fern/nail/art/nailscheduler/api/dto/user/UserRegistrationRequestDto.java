package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.common.annotation.Name;
import fern.nail.art.nailscheduler.common.annotation.PasswordMatchValidator;
import fern.nail.art.nailscheduler.common.annotation.Phone;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatchValidator(field = "password",
        fieldMatch = "repeatPassword")
public record UserRegistrationRequestDto(
        @NotBlank
        @Length(min = 5, max = 24)
        String username,

        @NotBlank
        @Length(min = 8, max = 24)
        String password,

        @NotBlank
        @Length(min = 8, max = 24)
        String repeatPassword,

        @Phone
        String phone,

        @Name
        @NotBlank
        String firstName,

        @Name
        String lastName,

        Long telegramId
) {
}
