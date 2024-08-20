package fern.nail.art.nailscheduler.annotation;

import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

class PasswordMatchValidator
        implements ConstraintValidator<FieldMatchValidator, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto userDto, ConstraintValidatorContext context) {
        return Objects.equals(userDto.password(), userDto.repeatPassword());
    }
}
