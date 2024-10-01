package fern.nail.art.nailscheduler.telegram.utils.menu.button;

import static fern.nail.art.nailscheduler.telegram.utils.Command.CHANGE_FIRST_NAME;
import static fern.nail.art.nailscheduler.telegram.utils.Command.CHANGE_LAST_NAME;
import static fern.nail.art.nailscheduler.telegram.utils.Command.CHANGE_USERNAME;
import static fern.nail.art.nailscheduler.telegram.utils.Command.LOGIN;
import static fern.nail.art.nailscheduler.telegram.utils.Command.MAIN;
import static fern.nail.art.nailscheduler.telegram.utils.Command.REGISTER;
import static fern.nail.art.nailscheduler.telegram.utils.Command.REGISTRATION;
import static fern.nail.art.nailscheduler.telegram.utils.Command.SAVE_USERNAME;
import static org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder;

import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
@RequiredArgsConstructor
public class AuthorizationButtonUtil {
    private static final String BUTTON_TO_BEGINNING = "button.to.beginning";
    private static final String BUTTON_USE = "button.use";
    private static final String BUTTON_CHANGE = "button.change";
    private static final String BUTTON_REGISTER = "button.register";
    private static final String BUTTON_LOGIN = "button.login";
    private static final String BUTTON_CHANGE_FIRST_NAME = "button.change.first.name";
    private static final String BUTTON_CHANGE_LAST_NAME = "button.change.last.name";
    private static final String BUTTON_CONFIRM = "button.confirm";
    private final LocalizationService localizationService;

    public InlineKeyboardButton toBeginning(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_TO_BEGINNING, locale))
                .callbackData(MAIN.getCommand())
                .build();
    }

    public InlineKeyboardButton saveUsername(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_USE, locale))
                .callbackData(SAVE_USERNAME.getCommand())
                .build();
    }

    public InlineKeyboardButton changeUsername(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_CHANGE, locale))
                .callbackData(CHANGE_USERNAME.getCommand())
                .build();
    }

    public InlineKeyboardButton register(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_REGISTER, locale))
                .callbackData(REGISTER.getCommand())
                .build();
    }

    public InlineKeyboardButton login(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_LOGIN, locale))
                .callbackData(LOGIN.getCommand())
                .build();
    }

    public InlineKeyboardButton changeFirstName(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_CHANGE_FIRST_NAME, locale))
                .callbackData(CHANGE_FIRST_NAME.getCommand())
                .build();
    }

    public InlineKeyboardButton changeLastName(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_CHANGE_LAST_NAME, locale))
                .callbackData(CHANGE_LAST_NAME.getCommand())
                .build();
    }

    public InlineKeyboardButton confirm(Locale locale) {
        return builder()
                .text(localizationService.localize(BUTTON_CONFIRM, locale))
                .callbackData(REGISTRATION.getCommand())
                .build();
    }
}
