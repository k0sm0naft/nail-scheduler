package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitingPasswordUpdateProcessor implements UpdateProcessor {
    private static final String ENTER_PASSWORD = "message.enter.password";
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.AWAITING_PASSWORD == user.getLocalState()
                && user instanceof AuthUser
                && update.hasMessage()
                && update.getMessage().hasText();
    }

    @Override
    public void process(Update update, User commonUser) {
        AuthUser user = (AuthUser) commonUser;
        String text;
        Locale locale = user.getLocale();

        user.setPassword(update.getMessage().getText());

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            messageService.editTextMessage(user, user.getMenuId(), text);
        } else {
            user.setLocalState(LocalState.PASSWORD_ACCEPTED);
            userService.saveTempUser(user);
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        }
    }
}
