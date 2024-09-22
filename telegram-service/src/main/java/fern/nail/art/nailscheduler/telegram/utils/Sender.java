package fern.nail.art.nailscheduler.telegram.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface Sender {
    void sendMessage(Long chatId, String message);
    void sendMenu(Long chatId, InlineKeyboardMarkup markup);
}