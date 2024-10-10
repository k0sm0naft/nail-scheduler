package fern.nail.art.nailscheduler.telegram.processor.impl.master;

import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MasterMainMenuUpdateProcessor implements UpdateProcessor {
    private static final String HELLO = "message.hello";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.MASTER_MENU
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        Locale locale = user.getLocale();
        String text = localizationService.localize(HELLO, locale).formatted(user.getFirstName())
                + System.lineSeparator() + "(it is dummy master menu)";
        Integer menuId = messageService.sendTextAndGetId(user, text);
        user.setMenuId(menuId);
        userService.saveTempUser(user);
    }
}
