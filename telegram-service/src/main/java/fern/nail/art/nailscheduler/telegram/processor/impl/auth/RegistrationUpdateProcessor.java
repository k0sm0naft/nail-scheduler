package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.CallbackQueryData;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class RegistrationUpdateProcessor implements UpdateProcessor {
    private static final String CHOSE_OPTION = "message.chose.option";
    private static final String ENTER_LOGIN = "message.enter.login";
    private static final String USE_FOR_LOGIN = "message.use.for.login";

    private final UserService userService;
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
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
            switch (CallbackQueryData.fromString(data)) {
                case REGISTRATION -> {
                    user = new AuthUser(user);
                    startRegistration(update, (AuthUser) user);
                }
                case SAVE_USERNAME -> saveUsername(update, (AuthUser) user);
                case CHANGE_USERNAME -> changeUsername(update, user);
            }
        }
        userService.saveTempUser(user);
    }

    private void saveUsername(Update update, AuthUser user) {
        String username = AbilityUtils.getUser(update).getUserName();
        user.setUsername(username);
        user.setLocalState(LocalState.SEND_PASSWORD_REQUEST);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void startRegistration(Update update, AuthUser user) {
        String username = AbilityUtils.getUser(update).getUserName();
        Locale locale = user.getLocale();

        user.setUsername(username);
        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            changeUsername(update, user);
        } else {
            String text = localizationService.localize(USE_FOR_LOGIN, locale).formatted(username);
            InlineKeyboardMarkup markup = menu.saveUsername(locale);
            messageService.editMenu(user, user.getMenuId(), text, markup);
        }
    }

    private void changeUsername(Update update, User user) {
        user.setLocalState(LocalState.SEND_USERNAME_REQUEST);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
