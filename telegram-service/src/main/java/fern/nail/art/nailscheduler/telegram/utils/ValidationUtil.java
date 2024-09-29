package fern.nail.art.nailscheduler.telegram.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    private final MessageSource messageSource;
    private final Validator validator;

    public ValidationUtil(@Qualifier("commonMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public <T> Optional<String> findViolationsOf(T object, Locale locale) {
        return findViolationsOf(object, null, locale);
    }

    public <T> Optional<String> findViolationsOf(T object, String propertyName, Locale locale) {
        Set<ConstraintViolation<T>> violations;

        if (propertyName == null) {
            violations = validator.validate(object);
        } else {
            violations = validator.validateProperty(object, propertyName);
        }

        if (!violations.isEmpty()) {
            String errorMessage =
                    violations.stream()
                              .map(ConstraintViolation::getMessage)
                              .map(this::extractKeyFromBrackets)
                              .map(message -> messageSource.getMessage(message, null, locale))
                              //todo format to red color for telegram
                              .collect(Collectors.joining(System.lineSeparator()));
            return Optional.of(errorMessage);
        }

        return Optional.empty();
    }

    private String extractKeyFromBrackets(String message) {
        if (message.startsWith("{") && message.endsWith("}")) {
            return message.substring(1, message.length() - 1);
        }

        return message;
    }
}
