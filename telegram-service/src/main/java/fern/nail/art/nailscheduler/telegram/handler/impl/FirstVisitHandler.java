package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
@RequiredArgsConstructor
public class FirstVisitHandler implements UpdateHandler {
    private final MessageService messageService;

    @Override
    public void handleUpdate(Update update, User user) {
        Long chatId = AbilityUtils.getChatId(update);

        messageService.sendMenu(chatId, "Выберите действие:", getMenu());
    }

    private static InlineKeyboardMarkup getMenu() {
        InlineKeyboardButton registerButton =
                InlineKeyboardButton.builder()
                                    .text("Зарегистрироваться")
                                    .callbackData("register")
                                    .build();
        InlineKeyboardButton loginButton =
                InlineKeyboardButton.builder()
                                    .text("Уже зарегистрирован")
                                    .callbackData("login")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(registerButton, loginButton))
                                   .build();
    }
}
