package fern.nail.art.nailscheduler.telegram.utils.menu;

import fern.nail.art.nailscheduler.telegram.utils.menu.button.AuthorizationButtonUtil;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
@RequiredArgsConstructor
public class AuthorizationMenuUtil {
    private final AuthorizationButtonUtil button;

    public InlineKeyboardMarkup beckToMainButton(Locale locale) {
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(button.toBeginning(locale)))
                                   .build();
    }

    public InlineKeyboardMarkup saveUsername(Locale locale) {
        InlineKeyboardRow row1 =
                new InlineKeyboardRow(button.saveUsername(locale), button.changeUsername(locale));
        InlineKeyboardRow row2 = new InlineKeyboardRow(button.toBeginning(locale));
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row1)
                                   .keyboardRow(row2)
                                   .build();
    }

    public InlineKeyboardMarkup registration(Locale locale) {
        InlineKeyboardRow row =
                new InlineKeyboardRow(button.register(locale), button.login(locale));
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row)
                                   .build();
    }

    public InlineKeyboardMarkup changeName(Locale locale) {
        InlineKeyboardRow row1 =
                new InlineKeyboardRow(button.changeFirstName(locale), button.changeLastName(locale));
        InlineKeyboardRow
                row2 = new InlineKeyboardRow(button.confirm(locale), button.toBeginning(locale));
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row1)
                                   .keyboardRow(row2)
                                   .build();
    }
}
