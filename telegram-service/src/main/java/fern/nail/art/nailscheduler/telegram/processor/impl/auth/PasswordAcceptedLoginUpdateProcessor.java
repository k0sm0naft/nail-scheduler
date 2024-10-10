package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PasswordAcceptedLoginUpdateProcessor implements UpdateProcessor {
    private static final String REPEAT = "message.repeat";
    private static final String WRONG_CREDENTIALS = "message.wrong.credentials";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.LOGIN == user.getGlobalState()
                && LocalState.PASSWORD_ACCEPTED == user.getLocalState()
                && user instanceof AuthUser
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        String text;
        Locale locale = user.getLocale();

        boolean isAuthenticated = userService.authenticate((AuthUser) user);
        if (isAuthenticated) {
            //todo replace it on userService if authentication was successful
            user.setGlobalState(GlobalState.IDLE);
            user.setLocalState(null);
            messageService.deleteMessage(user, user.getMenuId());
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        } else {
            user.setGlobalState(GlobalState.AUTHENTICATION);
            user.setLocalState(null);
            text = localizationService.localize(WRONG_CREDENTIALS, locale)
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, user.getMenuId(), text,
                    menu.authentication(locale));
            userService.saveTempUser(user);
        }
    }
}
