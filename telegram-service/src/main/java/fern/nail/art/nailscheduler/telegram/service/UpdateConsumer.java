package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.handler.impl.ClientVisitHandler;
import fern.nail.art.nailscheduler.telegram.handler.impl.FirstVisitHandler;
import fern.nail.art.nailscheduler.telegram.handler.impl.MasterVisitHandler;
import fern.nail.art.nailscheduler.telegram.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    //todo change consumer and process in multithreading
    // public class UpdateConsumer implements LongPollingUpdateConsumer {
    private final UserService userService;
    private final FirstVisitHandler firstVisitHandler;
    private final ClientVisitHandler clientVisitHandler;
    private final MasterVisitHandler masterVisitHandler;

    @Override
    public void consume(Update update) {
        User user = userService.getUser(update);
        Message message = update.getMessage();
        if (message != null
                && message.getText() != null
                && message.getText().startsWith("/start")) {
            userService.deleteTempUser(user);
        }

        switch (user.getRole()) {
            case UNKNOWN -> firstVisitHandler.handleUpdate(update, user);
            case CLIENT -> clientVisitHandler.handleUpdate(update, user);
            case MASTER -> masterVisitHandler.handleUpdate(update, user);
            default -> throw new RuntimeException("Can't handle role.");
        }
    }
}
