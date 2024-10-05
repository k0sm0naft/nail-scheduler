package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RegistrationFullNameAcceptedUpdateProcessor implements UpdateProcessor {
    private static final String MISTAKE_OCCURS = "message.mistake.occurs";
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && LocalState.FULL_NAME_ACCEPTED == user.getLocalState()
                && user instanceof RegisterUser
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();
        user.setLocalState(null);

        Optional<User> optionalUser = userService.register((RegisterUser) user);
        if (optionalUser.isPresent()) {
            user.setGlobalState(GlobalState.IDLE);
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        } else {
            user.setGlobalState(GlobalState.AUTHENTICATION);
            String text = localizationService.localize(MISTAKE_OCCURS, locale)
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, user.getMenuId(), text, menu.authentication(locale));
            userService.deleteTempUser(user);
        }
    }
}
