package fern.nail.art.nailscheduler.telegram.config;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {
    private final UpdateHandler updateHandler;

    @Bean
    public TelegramLongPollingBot longPullingBot(String botToken, String botUsername) {
        return new TelegramLongPollingBot(botToken) {
            @Override
            public void onUpdateReceived(Update update) {
                updateHandler.handleUpdate(update);
            }

            @Override
            public String getBotUsername() {
                return botUsername;
            }
        };
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername
    ) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(longPullingBot(botToken, botUsername));
        return telegramBotsApi;
    }
}
