package fern.nail.art.nailscheduler.telegram.processor.impl;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class IdleUpdateProcessor implements UpdateProcessor {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.IDLE
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        switch (user.getRole()) {
            case CLIENT -> user.setGlobalState(GlobalState.CLIENT_MENU);
            case MASTER -> user.setGlobalState(GlobalState.MASTER_MENU);
            case UNKNOWN -> user.setGlobalState(GlobalState.AUTHENTICATION);
            default -> throw new IllegalStateException("Unexpected role: " + user.getRole());
        }

        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
