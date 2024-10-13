package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CHANGE_FIRST_NAME;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CHANGE_LAST_NAME;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CONFIRM;
import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SendNameRequestUpdateProcessor implements UpdateProcessor {
    private static final String VALUE_MISSING = "---";
    private static final String CHANGE_NAMES = "message.change.names";
    private static final String ENTER_FIRST_NAME = "message.enter.first.name";
    private static final String ENTER_LAST_NAME = "message.enter.last.name";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.SEND_REQUEST_NAME == user.getLocalState();
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            switch (ButtonType.valueOf(data)) {
                case CHANGE_FIRST_NAME -> handleChangeFirsName(user);
                case CHANGE_LAST_NAME -> handleChangeLastName(user);
                case CONFIRM -> handleSaveFullName(update,user);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
        } else {
            sendRequest(user);
        }
    }

    private void handleChangeFirsName(User user) {
        user.setLocalState(LocalState.AWAITING_FIRST_NAME);
        userService.saveUser(user);

        String text = localizationService.localize(ENTER_FIRST_NAME, user.getLocale());
        messageService.editTextMessage(user, user.getMenuId(), text);
    }

    private void handleChangeLastName(User user) {
        user.setLocalState(LocalState.AWAITING_LAST_NAME);
        userService.saveUser(user);

        String text = localizationService.localize(ENTER_LAST_NAME, user.getLocale());
        messageService.editTextMessage(user, user.getMenuId(), text);
    }

    private void handleSaveFullName(Update update, User user) {
        user.setLocalState(LocalState.ACCEPTED_FULL_NAME);
        userService.saveUser(user);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void sendRequest(User user) {
        Locale locale = user.getLocale();

        if (hasViolations(user)) {
            return;
        }

        String lastName = user.getLastName() == null ? VALUE_MISSING : user.getLastName();
        String text = localizationService.localize(CHANGE_NAMES, locale)
                                  .formatted(lineSeparator(), user.getFirstName(), lastName);
        InlineKeyboardMarkup markup = markupFactory
                .create(List.of(CHANGE_FIRST_NAME, CHANGE_LAST_NAME, CONFIRM), locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
    }

    private boolean hasViolations(User user) {
        Optional<String> firstNameViolations =
                validationUtil.findViolationsOf(user, "firstName", user.getLocale());
        if (firstNameViolations.isPresent()) {
            handleChangeFirsName(user);
            return true;
        }

        Optional<String> lastNameViolations =
                validationUtil.findViolationsOf(user, "lastName", user.getLocale());
        if (lastNameViolations.isPresent()) {
            user.setLastName(null);
            userService.saveUser(user);
        }
        return false;
    }
}
