package fern.nail.art.nailscheduler.telegram.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MessageService {
    void sendText(Long chatId, String text);

    void sendMenu(Long chatId, String text, InlineKeyboardMarkup markup);

    void changeMenu(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup);
}
