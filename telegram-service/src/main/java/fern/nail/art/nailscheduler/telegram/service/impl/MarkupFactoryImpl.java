package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Function;
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
                                                        .map(buttonType -> createButton(buttonType,
                                                                locale))
                                                        .toList();
        return InlineKeyboardMarkup.builder()
                                   .keyboard(getRows(buttons))
                                   .build();
    }

    @Override
    public void setCallbackToButton(
            ButtonType buttonType, String callback, InlineKeyboardMarkup markup
    ) {
        markup.getKeyboard().stream()
              .flatMap(Collection::stream)
              .filter(button -> button.getCallbackData().equals(buttonType.name()))
              .findFirst()
              .orElseThrow(() -> new NoSuchElementException("No such button: " + buttonType))
              .setCallbackData(callback);
    }

    @Override
    public <T> void addCustomButtons(
            Function<T, String> buttonName,
            Function<T, String> callback,
            List<T> elements,
            InlineKeyboardMarkup markup
    ) {
        List<InlineKeyboardButton> newButtons =
                elements.stream()
                        .map(element -> createButton(
                                buttonName.apply(element),
                                callback.apply(element)
                        ))
                        .toList();

        List<InlineKeyboardRow> keyboardRows = new LinkedList<>(markup.getKeyboard());
        keyboardRows.addAll(getRows(newButtons));
        markup.setKeyboard(keyboardRows);
    }

    private List<InlineKeyboardRow> getRows(List<InlineKeyboardButton> buttons) {
        List<InlineKeyboardRow> rows = new LinkedList<>();
        InlineKeyboardRow row = null;

        for (int i = 0; i < buttons.size(); i++) {
            if (i % MAX_BUTTONS_IN_ROW == 0) {
                row = new InlineKeyboardRow();
                rows.add(row);
            }
            row.add(buttons.get(i));
        }

        return rows;
    }

    private InlineKeyboardButton createButton(ButtonType buttonType, Locale locale) {
        return InlineKeyboardButton.builder()
                                   .text(localizationService.localize(buttonType, locale))
                                   .callbackData(buttonType.name())
                                   .build();
    }

    private InlineKeyboardButton createButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                                   .text(text)
                                   .callbackData(callback)
                                   .build();
    }
}
