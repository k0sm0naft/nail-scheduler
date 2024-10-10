package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.exception.SendMessageException;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final TelegramClient telegramClient;

    @Override
    public Integer sendTextAndGetId(User user, String text) {
        return sendMenuAndGetId(user, text, null);
    }

    @Override
    public Integer sendMenuAndGetId(User user, String text, InlineKeyboardMarkup markup) {
        try {
            return telegramClient.execute(SendMessage.builder()
                                                     .chatId(user.getTelegramId())
                                                     .text(text)
                                                     .replyMarkup(markup)
                                                     .build())
                                 .getMessageId();
        } catch (TelegramApiException e) {
            throw new SendMessageException("Can't execute message. Cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void editTextMessage(User user, Integer messageId, String text) {
        editMenu(user, messageId, text, null);
    }

    @Override
    public void editMenu(User user, Integer messageId, String text, InlineKeyboardMarkup markup) {
        send(EditMessageText.builder()
                            .chatId(user.getTelegramId())
                            .messageId(messageId)
                            .text(text)
                            .replyMarkup(markup)
                            .build());
    }

    @Override
    public void deleteMessage(User user, Integer messageId) {
        send(DeleteMessage.builder()
                          .chatId(user.getTelegramId())
                          .messageId(messageId)
                          .build());
    }

    private void send(BotApiMethod<? extends Serializable> message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Can't execute message. Cause: {}", e.getMessage(), e);
        }
    }
}
