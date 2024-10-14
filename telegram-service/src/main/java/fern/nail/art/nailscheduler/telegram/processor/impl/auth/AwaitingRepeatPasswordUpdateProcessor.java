package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.MessageType.ENTER_PHONE;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.PASSWORD_ACCEPTED;

import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitingRepeatPasswordUpdateProcessor implements UpdateProcessor {
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

        user.setRepeatPassword(update.getMessage().getText());
        List<String> violations = validationUtil.findViolationsOf(user);

        if (violations.isEmpty()) {
            user.setLocalState(LocalState.AWAITING_PHONE);
            text = localizationService.localize(List.of(PASSWORD_ACCEPTED, ENTER_PHONE), locale);
        } else {
            user.setRepeatPassword(null);
            user.setLocalState(LocalState.AWAITING_PASSWORD);
            text = localizationService.localize(List.copyOf(violations), locale);
        }

        userService.saveUser(user);
        messageService.editTextMessage(user, user.getMenuId(), text);
    }
}
