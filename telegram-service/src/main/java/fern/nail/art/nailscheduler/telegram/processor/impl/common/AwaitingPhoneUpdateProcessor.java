package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitingPhoneUpdateProcessor implements UpdateProcessor {
    private static final String REPEAT = "message.repeat";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final UserService userService;
    private final ValidationUtil validationUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return LocalState.AWAITING_PHONE == user.getLocalState()
                && update.hasMessage();
    }

    @Override
    public void process(Update update, User user) {
        user.setPhone(update.getMessage().getText());
        String text;
        Locale locale = user.getLocale();

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        Integer menuId = user.getMenuId();
        if (violations.isPresent()) {
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            messageService.editTextMessage(user, menuId, text);
        } else {
            user.setLocalState(LocalState.SEND_NAME_REQUEST);
            userService.saveUser(user);

            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        }
    }
}
