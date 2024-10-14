package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import static fern.nail.art.nailscheduler.telegram.model.MessageType.PASSWORD_ACCEPTED;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.REPEAT_PASSWORD;

import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PasswordAcceptedRegistrationUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.REGISTRATION == user.getGlobalState()
                && LocalState.ACCEPTED_PASSWORD == user.getLocalState()
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        user.setLocalState(LocalState.AWAITING_REPEAT_PASSWORD);
        String text = localizationService
                .localize(List.of(PASSWORD_ACCEPTED, REPEAT_PASSWORD), user.getLocale());

        messageService.editTextMessage(user, user.getMenuId(), text);
        userService.saveUser(user);
    }
}
