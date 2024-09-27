package fern.nail.art.nailscheduler.telegram.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    private static ValidationUtil instance;

    private final jakarta.validation.Validator validator;

    private ValidationUtil() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public static synchronized ValidationUtil getInstance() {
        return instance == null ? new ValidationUtil() : instance;
    }

    public <T> Optional<String> findViolationsOf(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                                            .map(ConstraintViolation::getMessage)
                                            .collect(Collectors.joining(System.lineSeparator()));
            return Optional.of(errorMessage);
        }

        return Optional.empty();
    }

    public <T> Optional<String> findViolationsOf(T object, String propertyName) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, propertyName);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                                            .map(ConstraintViolation::getMessage)
                                            .collect(Collectors.joining(System.lineSeparator()));
            return Optional.of(errorMessage);
        }

        return Optional.empty();
    }
}
