package fern.nail.art.nailscheduler.telegram.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBotService {
    void sendMessage(Long chatId, String message);
    void updateMenu(Long chatId);
}
