package fern.nail.art.nailscheduler.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.constraints.Length;

@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Length(min = 3, max = 24)
@Pattern(regexp = "\\S*", message = "{validation.name.spaces}")
@Pattern(regexp = "[A-ZА-Я][a-zа-я]*", message = "{validation.name.capitals}")
public @interface Name {
    String message() default "Invalid name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
