package fern.nail.art.nailscheduler.telegram.menu;

import fern.nail.art.nailscheduler.telegram.model.Command;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public class MenuButtonManager {
    public InlineKeyboardMarkup getFirstMenuButtons() {
        InlineKeyboardButton localeButton =
                InlineKeyboardButton.builder()
                                    .text("Choose Locale")
                                    .callbackData(
                                            Command.CHOOSE_LOCALE.getCommand())
                                    .build();

        InlineKeyboardButton registerButton =
                InlineKeyboardButton.builder()
                                    .text("Register")
                                    .callbackData(
                                            Command.REGISTER.getCommand())
                                    .build();

        InlineKeyboardButton loginButton =
                InlineKeyboardButton.builder()
                                    .text("Login")
                                    .callbackData(
                                            Command.LOGIN.getCommand())
                                    .build();

        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(localeButton))
                                   .keyboardRow(new InlineKeyboardRow(registerButton, loginButton))
                                   .build();
    }

    public InlineKeyboardMarkup getMainMenuButtons() {
        return null;
    }

    public InlineKeyboardMarkup getLocaleMenuButtons() {
        InlineKeyboardButton enButton =
                InlineKeyboardButton.builder()
                                    .text("English")
                                    .callbackData(
                                            Command.MAIN_MENU.getCommand())
                                    .build();

        InlineKeyboardButton ruButton =
                InlineKeyboardButton.builder()
                                    .text("Русский")
                                    .callbackData(
                                            Command.MAIN_MENU.getCommand())
                                    .build();

        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(enButton, ruButton))
                                   .build();
    }
}
