package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SendUsernameRequestUpdateProcessor implements UpdateProcessor {
    private static final String ENTER_LOGIN = "message.enter.login";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final AuthorizationMenuUtil menu;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.SEND_USERNAME_REQUEST == user.getLocalState();
    }

    @Override
    public void process(Update update, User user) {
        String text = localizationService.localize(ENTER_LOGIN, user.getLocale());
        InlineKeyboardMarkup markup = menu.beckToMainButton(user.getLocale());
        user.setLocalState(LocalState.AWAITING_USERNAME);

        userService.saveTempUser(user);
        messageService.editMenu(user, user.getMenuId(), text, markup);
    }
}
