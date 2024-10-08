package fern.nail.art.nailscheduler.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotBlank
@Pattern(
    regexp = "^(\\+?\\d{2})?(\\(\\d{3}\\)|\\d{3})[-.\\s]*\\d{3}[-.\\s]*\\d{2,4}[-.\\s]*\\d{2,4}$",
    message = "{validation.phone.invalid}"
)
public @interface Phone {
    String message() default "Invalid phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
