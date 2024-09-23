package fern.nail.art.nailscheduler.telegram.handler;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    void handleUpdate(Update update, User user);
}
