package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
@RequiredArgsConstructor
public class ClientVisitHandler implements UpdateHandler {
    private final MessageService messageService;

    @Override
    public void handleUpdate(Update update, User user) {
        messageService.sendText(user.getTelegramId(),
                "Hello, %s!".formatted(user.getFirstName()));
    }

    public InlineKeyboardMarkup getMainMenu() {
        InlineKeyboardButton firstMainButton =
                InlineKeyboardButton.builder()
                                    .text("Главная кнопка")
                                    .callbackData("mainButton")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(firstMainButton))
                                   .build();
    }
}
