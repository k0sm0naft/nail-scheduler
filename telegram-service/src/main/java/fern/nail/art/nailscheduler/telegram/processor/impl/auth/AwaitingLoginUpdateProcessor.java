package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.MessageType.ENTER_PASSWORD;

import fern.nail.art.nailscheduler.telegram.model.AuthUser;
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
public class AwaitingLoginUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.AWAITING_USERNAME == user.getLocalState()
                && user instanceof AuthUser
                && update.hasMessage()
                && update.getMessage().hasText();
    }

    @Override
    public void process(Update update, User commonUser) {
        AuthUser user = (AuthUser) commonUser;
        String text;
        Locale locale = user.getLocale();

        user.setUsername(update.getMessage().getText());
        List<String> violations = validationUtil.findViolationsOf(user);

        if (violations.isEmpty()) {
            text = localizationService.localize(ENTER_PASSWORD, locale);
            user.setLocalState(LocalState.AWAITING_PASSWORD);
            userService.saveUser(user);
        } else {
            text = localizationService.localize(List.copyOf(violations), locale);
        }

        messageService.editTextMessage(user, user.getMenuId(), text);
    }
}
