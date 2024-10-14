package fern.nail.art.nailscheduler.telegram.processor.impl.common;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AwaitingPhoneUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
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

        List<String> violations = validationUtil.findViolationsOf(user);
        Integer menuId = user.getMenuId();
        if (!violations.isEmpty()) {
            text = localizationService.localize(List.copyOf(violations), locale);
            messageService.editTextMessage(user, menuId, text);
        } else {
            user.setLocalState(LocalState.ACCEPTED_PHONE);
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        }
    }
}
