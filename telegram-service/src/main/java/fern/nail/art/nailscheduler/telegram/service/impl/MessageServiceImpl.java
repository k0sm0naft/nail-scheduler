package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.exception.SendMessageException;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final TelegramClient telegramClient;

    @Override
    public void sendText(Long chatId, String text) {
        send(SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .build());
    }

    @Override
    public void sendMenu(Long chatId, String text, InlineKeyboardMarkup markup) {
        send(SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .replyMarkup(markup)
                        .build());
    }

    @Override
    public void changeMenu(
            Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup
    ) {
        send(EditMessageText.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .text(text)
                            .replyMarkup(markup)
                            .build());
    }

    private void send(BotApiMethod<? extends Serializable> message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new SendMessageException("Can't execute message.", e);
        }
    }
}
