package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
@RequiredArgsConstructor
public class MarkupFactoryImpl implements MarkupFactory {
    private static final int MAX_BUTTONS_IN_ROW = 2;

    private final LocalizationService localizationService;

    public InlineKeyboardMarkup create(List<ButtonType> buttonTypes, Locale locale) {
        List<InlineKeyboardButton> buttons = buttonTypes.stream()
                .map(buttonType -> createButton(buttonType, locale))
                .toList();
        List<InlineKeyboardRow> rows = new LinkedList<>();
        InlineKeyboardRow row = null;

        for (int i = 0; i < buttons.size(); i++) {
            if (i % MAX_BUTTONS_IN_ROW == 0) {
                row = new InlineKeyboardRow();
                rows.add(row);
            }
            row.add(buttons.get(i));
        }

        return InlineKeyboardMarkup.builder()
                                   .keyboard(rows)
                                   .build();
    }

    private InlineKeyboardButton createButton(ButtonType buttonType, Locale locale) {
        return InlineKeyboardButton.builder()
                                   .text(localizationService.localize(
                                           buttonType.getLocalizationKey(), locale))
                                   .callbackData(buttonType.name())
                                   .build();
    }
}
