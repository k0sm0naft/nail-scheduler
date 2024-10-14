package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.model.Localizable;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
public class LocalizationServiceImpl implements LocalizationService {
    private final MessageSource messageSource;

    public LocalizationServiceImpl(@Qualifier("commonMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String localize(String localizationKey, Locale locale) {
        return getLocalizedMessage(localizationKey, locale);
    }

    @Override
    public String localize(Localizable localizable, Locale locale) {
        String key = localizable.getLocalizationKey();
        return getLocalizedMessage(key, locale);
    }

    @Override
    public String localize(Collection<Object> localizableElements, Locale locale) {
        return localizableElements
                .stream()
                .map(element ->
                        getLocalizedMessage(locale, element))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String getLocalizedMessage(Locale locale, Object element) {
        return switch (element) {
            case Localizable localizable -> localize(localizable, locale);
            case String string -> localize(string, locale);
            default -> throw new IllegalArgumentException(
                    "Unsupported element type: " + element.getClass().getName());
        };
    }

    @Nullable
    private String getLocalizedMessage(String key, Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            return key;
        }
    }
}
