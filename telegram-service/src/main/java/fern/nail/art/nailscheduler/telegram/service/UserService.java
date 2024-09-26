package fern.nail.art.nailscheduler.telegram.service;

import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {
    User getUser(Update update);

    User saveTempUser(User user);

    User getTempUser(User user);

    @Cacheable(value = "telegramNewUserCache", key = "#user.telegramId")
    void deleteTempUser(User user);

    Optional<User> registerUser(RegisterUser user);

    boolean authenticateUser(LoginUser user);
}
