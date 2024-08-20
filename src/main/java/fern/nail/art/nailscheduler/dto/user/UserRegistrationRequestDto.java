package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.annotation.FieldMatchValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@FieldMatchValidator(field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords don't match!")
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

        @NotBlank
        @Pattern(regexp = "^(\\+?\\d{2})?(\\(\\d{3}\\)"
                + "|\\d{3})[-.\\s]*\\d{3}[-.\\s]*\\d{2,4}[-.\\s]*\\d{2,4}$",
                message = "Number should contain 10 or 12 digits")
        String phone,

        @NotBlank
        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*",
                message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        String firstName,

        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*",
                message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        String lastName
) {
}
