package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.CallbackQueryData;
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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SendNameRequestUpdateProcessor implements UpdateProcessor {
    private static final String VALUE_MISSING = "---";
    private static final String CHANGE_NAMES = "message.change.names";
    private static final String ENTER_FIRST_NAME = "message.enter.first.name";
    private static final String ENTER_LAST_NAME = "message.enter.last.name";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.SEND_NAME_REQUEST == user.getLocalState();
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            switch (CallbackQueryData.fromString(data)) {
                case CHANGE_FIRST_NAME -> handleChangeFirsName(user);
                case CHANGE_LAST_NAME -> handleChangeLastName(user);
                case SAVE_FULL_NAME -> handleSaveFullName(update,user);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
        } else {
            sendRequest(user);
        }
    }

    private void handleChangeFirsName(User user) {
        user.setLocalState(LocalState.AWAITING_FIRST_NAME);
        userService.saveTempUser(user);

        String text = localizationService.localize(ENTER_FIRST_NAME, user.getLocale());
        messageService.editTextMessage(user, user.getMenuId(), text);
    }

    private void handleChangeLastName(User user) {
        user.setLocalState(LocalState.AWAITING_LAST_NAME);
        userService.saveTempUser(user);

        String text = localizationService.localize(ENTER_LAST_NAME, user.getLocale());
        messageService.editTextMessage(user, user.getMenuId(), text);
    }

    private void handleSaveFullName(Update update, User user) {
        user.setLocalState(LocalState.FULL_NAME_ACCEPTED);
        userService.saveTempUser(user);
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
        messageService.editMenu(user, user.getMenuId(), text, menu.changeName(locale));
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
            userService.saveTempUser(user);
        }
        return false;
    }
}
