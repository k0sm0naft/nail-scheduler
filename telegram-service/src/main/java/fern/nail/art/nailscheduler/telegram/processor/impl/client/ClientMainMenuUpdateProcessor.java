package fern.nail.art.nailscheduler.telegram.processor.impl.client;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.CallbackQueryData;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class ClientMainMenuUpdateProcessor implements UpdateProcessor {
    private static final String HELLO = "message.hello";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.CLIENT_MENU
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();
        String text = localizationService.localize(HELLO, locale).formatted(user.getFirstName())
                + System.lineSeparator() + "(it is dummy client menu)";
        messageService.sendMenu(user, text, menu.beckToMainButton(locale));
    }
}
