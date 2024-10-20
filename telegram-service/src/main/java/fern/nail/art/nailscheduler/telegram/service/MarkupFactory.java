package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MarkupFactory {
    InlineKeyboardMarkup create(List<ButtonType> buttons, Locale locale);

    void setCallbackToButton(ButtonType buttonType, String callback, InlineKeyboardMarkup markup);

    <T> void addCustomButtons(
            Function<T, String> buttonName,
            Function<T, String> callback,
            List<T> elements, InlineKeyboardMarkup markup
    );
}
