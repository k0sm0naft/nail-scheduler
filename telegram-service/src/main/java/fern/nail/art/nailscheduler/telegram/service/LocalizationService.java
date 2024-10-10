package fern.nail.art.nailscheduler.telegram.service;

import java.util.Locale;

public interface LocalizationService {
    String localize(String text, Locale locale);
}
