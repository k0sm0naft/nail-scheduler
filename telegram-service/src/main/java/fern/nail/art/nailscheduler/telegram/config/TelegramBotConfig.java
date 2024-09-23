package fern.nail.art.nailscheduler.telegram.config;

import fern.nail.art.nailscheduler.telegram.service.UpdateConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramBotConfig {
    private final UpdateConsumer updateConsumer;

    public TelegramBotConfig(@Lazy UpdateConsumer updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    @Bean
    public BotSession registerBot(@Value("${telegram.bot.token}") String botToken) throws Exception {
        try {
            TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
            return application.registerBot(botToken, updateConsumer);
        } catch (Exception e) {
            throw new Exception("Can't register TelegramBot.", e);
        }
    }

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot.token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }
}
