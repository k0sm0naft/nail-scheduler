package fern.nail.art.nailscheduler.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class EnumSizeValidator implements ConstraintValidator<ValidEnumSize, Set<?>> {

    private int expectedSize;

    @Override
    public void initialize(ValidEnumSize constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        expectedSize = enumClass.getEnumConstants().length;
    }

    @Override
    public boolean isValid(Set<?> value, ConstraintValidatorContext context) {
        return value != null && value.size() == expectedSize;
    }
}
