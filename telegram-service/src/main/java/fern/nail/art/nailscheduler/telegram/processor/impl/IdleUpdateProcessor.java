package fern.nail.art.nailscheduler.telegram.processor.impl;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class IdleUpdateProcessor implements UpdateProcessor {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.IDLE
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        switch (user.getRole()) {
            case CLIENT -> handleClient(update, user);
            case MASTER -> handleMaster(update, user);
            case UNKNOWN -> handleUnknown(update, user);
            default -> throw new IllegalStateException("Unexpected role: " + user.getRole());
        }
    }

    private void handleClient(Update update, User user) {
        user.setGlobalState(GlobalState.CLIENT_MENU);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void handleMaster(Update update, User user) {
        user.setGlobalState(GlobalState.MASTER_MENU);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void handleUnknown(Update update, User user) {
        user.setGlobalState(GlobalState.AUTHENTICATION);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
