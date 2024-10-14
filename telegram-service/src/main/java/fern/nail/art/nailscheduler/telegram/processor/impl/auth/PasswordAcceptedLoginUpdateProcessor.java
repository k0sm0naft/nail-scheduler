package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.LOGIN;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.REGISTRATION;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.REPEAT;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.WRONG_CREDENTIALS;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
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
public class PasswordAcceptedLoginUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.LOGIN == user.getGlobalState()
                && LocalState.ACCEPTED_PASSWORD == user.getLocalState()
                && user instanceof AuthUser
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();

        boolean isAuthenticated = userService.authenticate((AuthUser) user);
        if (isAuthenticated) {
            messageService.deleteMessage(user, user.getMenuId());
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        } else {
            String text = localizationService.localize(List.of(WRONG_CREDENTIALS, REPEAT), locale);
            InlineKeyboardMarkup markup =
                    markupFactory.create(List.of(REGISTRATION, LOGIN), locale);

            messageService.editMenu(user, user.getMenuId(), text, markup);
        }
    }
}
