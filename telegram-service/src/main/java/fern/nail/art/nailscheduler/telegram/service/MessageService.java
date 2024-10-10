package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MessageService {
    Integer sendTextAndGetId(User user, String text);

    Integer sendMenuAndGetId(User user, String text, InlineKeyboardMarkup markup);

    void editTextMessage(User user, Integer messageId, String text);

    void editMenu(User user, Integer messageId, String text, InlineKeyboardMarkup markup);

    void deleteMessage(User user, Integer messageId);
}
