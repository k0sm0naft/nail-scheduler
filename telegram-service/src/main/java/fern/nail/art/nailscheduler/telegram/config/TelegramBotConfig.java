package fern.nail.art.nailscheduler.telegram.config;

import static fern.nail.art.nailscheduler.telegram.model.Command.BOT_INFO;
import static fern.nail.art.nailscheduler.telegram.model.Command.CONTACTS;
import static fern.nail.art.nailscheduler.telegram.model.Command.INFO;
import static fern.nail.art.nailscheduler.telegram.model.Command.PRICE;
import static fern.nail.art.nailscheduler.telegram.model.Command.START;

import fern.nail.art.nailscheduler.telegram.service.UpdateConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@EnableScheduling
public class TelegramBotConfig {
    private final UpdateConsumer updateConsumer;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotConfig(@Lazy UpdateConsumer updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    @Bean
    public BotSession botSession() throws Exception {
        try {
            return new TelegramBotsLongPollingApplication().registerBot(botToken, updateConsumer);
        } catch (Exception e) {
            throw new Exception("Can't register TelegramBot.", e);
        }
    }

    @Bean
    public TelegramClient telegramClient() throws TelegramApiException {
        OkHttpTelegramClient client = new OkHttpTelegramClient(botToken);
        setBotCommands(client);
        return client;
    }

    public void setBotCommands(TelegramClient client) throws TelegramApiException {
        client.execute(
                SetMyCommands.builder()
                             .command(new BotCommand(START.get(), START.getDescription()))
                             .command(new BotCommand(INFO.get(), INFO.getDescription()))
                             .command(new BotCommand(BOT_INFO.get(), BOT_INFO.getDescription()))
                             .command(new BotCommand(CONTACTS.get(), CONTACTS.getDescription()))
                             .command(new BotCommand(PRICE.get(), PRICE.getDescription()))
                             .build()
        );
    }
}
