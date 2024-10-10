package fern.nail.art.nailscheduler.telegram.processor.impl.auth;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
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
public class LoginUpdateProcessor implements UpdateProcessor {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return GlobalState.LOGIN == user.getGlobalState()
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        user.setLocalState(LocalState.SEND_USERNAME_REQUEST);

        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, new AuthUser(user)));
    }
}
