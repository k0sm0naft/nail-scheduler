package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import java.util.Locale;
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
    public String localize(String text, Locale locale) {
        try {
            return messageSource.getMessage(text, null, locale);
        } catch (NoSuchMessageException e) {
            return text;
        }
    }
}
