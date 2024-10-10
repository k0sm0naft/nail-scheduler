package fern.nail.art.nailscheduler.telegram.utils.menu;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
@RequiredArgsConstructor
public class AuthorizationMenuUtil {
    private final ButtonUtil button;

    public InlineKeyboardMarkup saveUsername(Locale locale) {
        InlineKeyboardRow row1 =
                new InlineKeyboardRow(button.saveUsername(locale), button.changeUsername(locale));

        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row1)
                                   .build();
    }

    public InlineKeyboardMarkup authentication(Locale locale) {
        InlineKeyboardRow row =
                new InlineKeyboardRow(button.register(locale), button.login(locale));

        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row)
                                   .build();
    }

    public InlineKeyboardMarkup changeName(Locale locale) {
        InlineKeyboardRow row1 =
                new InlineKeyboardRow(button.changeFirstName(locale), button.changeLastName(locale));

        InlineKeyboardRow row2 =
                new InlineKeyboardRow(button.confirm(locale));

        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(row1)
                                   .keyboardRow(row2)
                                   .build();
    }
}
