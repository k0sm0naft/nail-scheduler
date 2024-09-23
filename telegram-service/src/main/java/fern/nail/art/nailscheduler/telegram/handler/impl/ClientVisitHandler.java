package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class ClientVisitHandler implements UpdateHandler {
    @Override
    public void handleUpdate(Update update, User user) {

    }
}
