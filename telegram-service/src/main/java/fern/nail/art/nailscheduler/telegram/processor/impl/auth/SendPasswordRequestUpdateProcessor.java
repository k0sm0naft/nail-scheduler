package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SendPasswordRequestUpdateProcessor implements UpdateProcessor {
    private static final String ENTER_PASSWORD = "message.enter.password";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.SEND_REQUEST_PASSWORD == user.getLocalState();
    }

    @Override
    public void process(Update update, User user) {
        String text = localizationService.localize(ENTER_PASSWORD, user.getLocale());
        user.setLocalState(LocalState.AWAITING_PASSWORD);

        userService.saveUser(user);
        messageService.editTextMessage(user, user.getMenuId(), text);
    }
}
