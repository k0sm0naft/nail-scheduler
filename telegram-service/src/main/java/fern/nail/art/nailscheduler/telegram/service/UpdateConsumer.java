package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    //todo change consumer and process in multithreading
    // public class UpdateConsumer implements LongPollingUpdateConsumer {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageService messageService;

    @Override
    public void consume(Update update) {
        User user = userService.getUser(update);

        if (update.hasMessage()) {
            messageService.deleteMessage(user, update.getMessage().getMessageId());

            if (update.getMessage().isCommand()) {
                user.setGlobalState(GlobalState.COMMAND);
                user.setLocalState(null);
            }
        }

        if (update.hasCallbackQuery() && user.getMenuId() == null) {
            user.setMenuId(update.getCallbackQuery().getMessage().getMessageId());
        }

        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
