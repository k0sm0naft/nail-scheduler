package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.CallbackQueryData;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class AuthenticationUpdateProcessor implements UpdateProcessor {
    //todo create enum for messages
    private static final String CHOSE_OPTION = "message.chose.option";

    private final UserService userService;
    private final MessageService messageService;
    private final LocalizationService localizationService;
    //todo relocate menu creation to processors itself
    private final AuthorizationMenuUtil menu;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.AUTHENTICATION
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {

            String data = update.getCallbackQuery().getData();
            switch (CallbackQueryData.fromString(data)) {
                case REGISTRATION -> user.setGlobalState(GlobalState.REGISTRATION);
                case LOGIN -> user.setGlobalState(GlobalState.LOGIN);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));

        } else {
            sendAuthMenu(user);
        }
    }

    private void sendAuthMenu(User user) {
        String text = localizationService.localize(CHOSE_OPTION, user.getLocale());
        InlineKeyboardMarkup markup = menu.authentication(user.getLocale());

        Integer menuId = messageService.sendMenuAndGetId(user, text, markup);
        user.setMenuId(menuId);
        userService.saveUser(user);
    }
}
