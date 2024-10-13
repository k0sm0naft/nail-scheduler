package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PasswordAcceptedRegistrationUpdateProcessor implements UpdateProcessor {
    private static final String PASSWORD_ACCEPTED = "message.password.accepted";
    private static final String REPEAT_PASSWORD = "message.repeat.password";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && LocalState.ACCEPTED_PASSWORD == user.getLocalState()
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();

        user.setLocalState(LocalState.AWAITING_REPEAT_PASSWORD);

        String text = localizationService.localize(PASSWORD_ACCEPTED, locale)
                + localizationService.localize(REPEAT_PASSWORD, locale);
        messageService.editTextMessage(user, user.getMenuId(), text);
        userService.saveUser(user);
    }
}
