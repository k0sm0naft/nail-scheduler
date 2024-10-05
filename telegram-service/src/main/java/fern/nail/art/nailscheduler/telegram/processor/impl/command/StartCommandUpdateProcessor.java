package fern.nail.art.nailscheduler.telegram.processor.impl.command;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.Command;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartCommandUpdateProcessor extends AbstractCommandUpdateProcessor {
    private final MessageService messageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected Command command() {
        return Command.START;
    }

    @Override
    public void process(Update update, User user) {
        clearPreviousMenu(user, messageService);

        user.setGlobalState(GlobalState.IDLE);
        user.setLocalState(null);

        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
