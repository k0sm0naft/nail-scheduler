package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitingRepeatPasswordUpdateProcessor implements UpdateProcessor {
    private static final String PASSWORD_ACCEPTED = "message.password.accepted";
    private static final String ENTER_PHONE = "message.enter.phone";
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && LocalState.AWAITING_REPEAT_PASSWORD == user.getLocalState()
                && user instanceof AuthUser
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User commonUser) {
        AuthUser user = (AuthUser) commonUser;
        String text;
        Locale locale = user.getLocale();
        Integer messageId = user.getMenuId();

        user.setRepeatPassword(update.getMessage().getText());
        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            user.setRepeatPassword(null);
            user.setLocalState(LocalState.AWAITING_PASSWORD);
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
        } else {
            user.setLocalState(LocalState.AWAITING_PHONE);
            text = localizationService.localize(PASSWORD_ACCEPTED, locale)
                    + lineSeparator() + localizationService.localize(ENTER_PHONE, locale);
        }
        userService.saveTempUser(user);
        messageService.editTextMessage(user, messageId, text);
    }
}
