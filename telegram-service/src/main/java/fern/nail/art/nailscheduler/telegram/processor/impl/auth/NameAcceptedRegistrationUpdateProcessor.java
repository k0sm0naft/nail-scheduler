package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.LOGIN;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.REGISTRATION;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.RegistrationResult;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class NameAcceptedRegistrationUpdateProcessor implements UpdateProcessor {
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && LocalState.ACCEPTED_FULL_NAME == user.getLocalState();
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();
        user.setGlobalState(GlobalState.IDLE);
        user.setLocalState(null);

        RegistrationResult registrationResult = userService.register((AuthUser) user);
        if (registrationResult.hasUserId()) {
            user.setUserId(registrationResult.userId());
            user.setRole(User.Role.CLIENT);
            messageService.deleteMessage(user, user.getMenuId());
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        } else {
            String text = registrationResult.errorMessage()
                    + localizationService.localize(REPEAT, locale);
            InlineKeyboardMarkup markup =
                    markupFactory.create(List.of(REGISTRATION, LOGIN), locale);

            userService.saveUser(user);
            messageService.editMenu(user, user.getMenuId(), text, markup);
        }
    }
}
