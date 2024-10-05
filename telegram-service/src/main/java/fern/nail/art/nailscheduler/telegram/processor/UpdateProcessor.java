package fern.nail.art.nailscheduler.telegram.processor;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessor {
    boolean canProcess(Update update, User user);

    void process(Update update, User user);
}
