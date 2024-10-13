package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import java.util.List;
import java.util.Locale;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MarkupFactory {
    InlineKeyboardMarkup create(List<ButtonType> buttons, Locale locale);
}
