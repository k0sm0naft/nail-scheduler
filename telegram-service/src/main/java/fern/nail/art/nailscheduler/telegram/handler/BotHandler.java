package fern.nail.art.nailscheduler.telegram.handler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BotHandler extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

    private BotHandler(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);

            if (messageText.equals("/start")) {
                message.setText("Hello, world!");
            } else {
                message.setText("Under construction!");
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException("Can't send message!");
            }
        }
    }
}
