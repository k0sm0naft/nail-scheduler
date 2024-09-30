package fern.nail.art.nailscheduler.telegram.utils;

import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;
    private final LocalizationService localizationService;

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
                              .map(message -> localizationService.localize(message, locale))
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
