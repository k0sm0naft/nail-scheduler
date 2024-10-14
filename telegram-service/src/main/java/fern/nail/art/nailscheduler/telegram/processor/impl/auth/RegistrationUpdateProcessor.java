package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CHANGE_USERNAME;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.CONFIRM;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.USE_FOR_LOGIN;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class RegistrationUpdateProcessor implements UpdateProcessor {
    private final UserService userService;
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            switch (ButtonType.valueOf(data)) {
                case REGISTRATION -> {
                    user = new AuthUser(user);
                    startRegistration(update, (AuthUser) user);
                }
                case CONFIRM -> saveUsername(update, (AuthUser) user);
                case CHANGE_USERNAME -> changeUsername(update, user);
                default -> throw new IllegalStateException("Unexpected callback: " + data);
            }
        }
        userService.saveUser(user);
    }

    private void saveUsername(Update update, AuthUser user) {
        String username = AbilityUtils.getUser(update).getUserName();
        user.setUsername(username);
        user.setLocalState(LocalState.SEND_REQUEST_PASSWORD);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void startRegistration(Update update, AuthUser user) {
        String username = AbilityUtils.getUser(update).getUserName();
        Locale locale = user.getLocale();

        user.setUsername(username);
        List<String> violations = validationUtil.findViolationsOf(user);
        if (!violations.isEmpty()) {
            changeUsername(update, user);
        } else {
            String text = localizationService.localize(USE_FOR_LOGIN, locale).formatted(username);
            InlineKeyboardMarkup markup =
                    markupFactory.create(List.of(CONFIRM, CHANGE_USERNAME), locale);

            messageService.editMenu(user, user.getMenuId(), text, markup);
        }
    }

    private void changeUsername(Update update, User user) {
        user.setLocalState(LocalState.SEND_REQUEST_USERNAME);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
