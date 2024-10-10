package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.RegistrationResult;
import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {
    User getUser(Update update);

    User saveTempUser(User user);

    User getTempUser(User user);

    void deleteTempUser(User user);

    RegistrationResult register(AuthUser user);

    boolean authenticate(AuthUser user);
}
