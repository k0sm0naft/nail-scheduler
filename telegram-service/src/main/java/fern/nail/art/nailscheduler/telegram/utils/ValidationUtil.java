package fern.nail.art.nailscheduler.telegram.utils;

import fern.nail.art.nailscheduler.telegram.model.MessageType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;

    public <T> List<String> findViolationsOf(T object) {
        return findViolationsOf(object, null);
    }

    public <T> List<String> findViolationsOf(T object, String propertyName) {
        Set<ConstraintViolation<T>> violations;

        if (propertyName == null) {
            violations = validator.validate(object);
        } else {
            violations = validator.validateProperty(object, propertyName);
        }

        List<String> violationKeys = violations.stream()
                                      .map(ConstraintViolation::getMessage)
                                      .map(this::extractKeyFromBrackets)
                                      .collect(Collectors.toList());
        if (violations.isEmpty()) {
            return violationKeys;
        }

        violationKeys.add(MessageType.REPEAT.getLocalizationKey());
        return violationKeys;

    }

    private String extractKeyFromBrackets(String message) {
        if (message.startsWith("{") && message.endsWith("}")) {
            return message.substring(1, message.length() - 1);
        }

        return message;
    }
}
