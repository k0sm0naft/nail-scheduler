package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.UserClient;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;
    private final UserMapper userMapper;

    @Override
    @CachePut(value = "telegramUserCache", key = "#result.id")
    public User getUser(Update update) {
        User user = userMapper.telegramUserToUser(AbilityUtils.getUser(update));
        user.setChatId(AbilityUtils.getChatId(update));
        return userClient.findUserByTelegramId(user.getTelegramId())
                         .orElseGet(() -> generateTempUser(user));
    }

    @Override
    @CachePut(value = "telegramUserCache", key = "telegramId")
    public User saveTelegramUser(User user) {
        return user;
    }

    @Override
    public String registerUser(User user) {
        return "registration message";
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return true;
    }

    private User generateTempUser(User user) {
        user.setRole(User.Role.UNKNOWN);
        return user;
    }
}
