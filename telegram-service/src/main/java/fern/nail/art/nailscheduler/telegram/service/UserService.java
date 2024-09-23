package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {
    User getUser(Update chatId);

    User saveTelegramUser(User user);

    Object registerUser(User user);

    boolean authenticateUser(String username, String password);
}
