package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CHANGE_FIRST_NAME;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CHANGE_LAST_NAME;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CONFIRM;
import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private final MarkupFactory markupFactory;
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
        user.setLocalState(LocalState.SEND_REQUEST_NAME);

        if (user.getLocalState() == LocalState.AWAITING_FIRST_NAME) {
            updateUserName(user, user::setFirstName, user::getFirstName, update);
        } else {
            updateUserName(user, user::setLastName, user::getLastName, update);
        }
    }

    private void updateUserName(
            User user, Consumer<String> setName, Supplier<String> getName, Update update
    ) {
        String oldName = getName.get();
        setName.accept(update.getMessage().getText());

        Locale locale = user.getLocale();
        Optional<String> violations = validationUtil.findViolationsOf(user, locale);

        if (violations.isPresent()) {
            setName.accept(oldName);
            String text = violations.get() + lineSeparator() +
                    localizationService.localize(REPEAT, locale);
            InlineKeyboardMarkup markup = markupFactory
                    .create(List.of(CHANGE_FIRST_NAME, CHANGE_LAST_NAME, CONFIRM), locale);

            messageService.editMenu(user, user.getMenuId(), text, markup);
        } else {
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        }

        userService.saveUser(user);
    }
}
