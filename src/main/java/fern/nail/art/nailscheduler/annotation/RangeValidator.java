package fern.nail.art.nailscheduler.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeRangeValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface RangeValidator {
    String message() default "Range is wrong!";
    String start();
    String end();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
