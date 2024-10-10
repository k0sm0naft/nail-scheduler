package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class AwaitingNameUpdateProcessor implements UpdateProcessor {
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        boolean isNameAwaiting = LocalState.AWAITING_FIRST_NAME == user.getLocalState()
                || LocalState.AWAITING_LAST_NAME == user.getLocalState();
        return isNameAwaiting && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        String name = update.getMessage().getText();
        if (LocalState.AWAITING_FIRST_NAME == user.getLocalState()) {
            user.setFirstName(name);
        } else {
            user.setLastName(name);
        }

        Locale locale = user.getLocale();
        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            String text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            InlineKeyboardMarkup markup = menu.changeName(user.getLocale());
            messageService.editMenu(user, user.getMenuId(), text, markup);
        } else {
            user.setLocalState(LocalState.SEND_NAME_REQUEST);
            userService.saveUser(user);
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        }
    }
}
