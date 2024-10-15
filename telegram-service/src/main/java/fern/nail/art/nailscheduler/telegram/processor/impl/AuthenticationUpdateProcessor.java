package fern.nail.art.nailscheduler.telegram.processor.impl;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AuthenticationUpdateProcessor implements UpdateProcessor {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.AUTHENTICATION
                && user.getLocalState() == null
                && update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        switch (ButtonType.valueOf(data)) {
            case REGISTRATION -> user.setGlobalState(GlobalState.REGISTRATION);
            case LOGIN -> user.setGlobalState(GlobalState.LOGIN);
            default -> throw new IllegalArgumentException("Unknown callback data: " + data);
        }
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
