package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.LOGIN;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.REGISTRATION;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
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
public class AuthenticationUpdateProcessor implements UpdateProcessor {
    //todo create enum for messages
    private static final String CHOSE_OPTION = "message.chose.option";

    private final UserService userService;
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
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
            switch (ButtonType.valueOf(data)) {
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
        Locale locale = user.getLocale();
        String text = localizationService.localize(CHOSE_OPTION, locale);
        InlineKeyboardMarkup markup =
                markupFactory.create(List.of(REGISTRATION, LOGIN), locale);

        Integer menuId = messageService.sendMenuAndGetId(user, text, markup);
        user.setMenuId(menuId);
        userService.saveUser(user);
    }
}
