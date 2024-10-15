package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.Localizable;
import java.util.Collection;
import java.util.Locale;

public interface LocalizationService {
    String localize(String localizationKey, Locale locale);

    String localize(Localizable localizable, Locale locale);

    //todo change to Collection<Localizable> and create model for localizable strings
    String localize(Collection<Object> localizableElements, Locale locale);
}
