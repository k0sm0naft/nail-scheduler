package fern.nail.art.nailscheduler.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanUtils;

public class PasswordValidator implements ConstraintValidator<PasswordMatchValidator, Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(PasswordMatchValidator constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        try {
            Object fieldValue = BeanUtils
                    .getPropertyDescriptor(dto.getClass(), field)
                    .getReadMethod()
                    .invoke(dto);
            Object fieldMatchValue = BeanUtils
                    .getPropertyDescriptor(dto.getClass(), fieldMatch)
                    .getReadMethod()
                    .invoke(dto);

            return Objects.equals(fieldValue, fieldMatchValue);
        } catch (Exception e) {
            return false;
        }
    }
}
