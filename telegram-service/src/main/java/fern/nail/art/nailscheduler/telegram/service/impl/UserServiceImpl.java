package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.UserClient;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;
    private final UserMapper userMapper;

    @Override
//    @Cacheable(value = "telegramUserCache", key = "#result.telegramId")
    public User getUser(Update update) {
        User user = userMapper.telegramUserToUser(AbilityUtils.getUser(update));
        return userClient.findUserByTelegramId(user.getTelegramId())
                         .orElseGet(() -> userMapper.createTempUser(user, update));
    }

    @Override
    @CachePut(value = "telegramNewUserCache", key = "#user.telegramId")
    public User saveTempUser(User user) {
        return user;
    }

    @Override
    @Cacheable(value = "telegramNewUserCache", key = "#user.telegramId")
    public User getTempUser(User user) {
        return user;
    }

    @Override
    @Caching( evict = {
            @CacheEvict(value = "telegramUserCache", key = "#user.telegramId"),
            @CacheEvict(value = "telegramNewUserCache", key = "#user.telegramId")
    })
    public void deleteTempUser(User user) {
    }

    @Override
    @CachePut(value = "telegramUserCache", key = "#user.telegramId")
    public Optional<User> registerUser(RegisterUser user) {
        return userClient.registerUser(user).map(userId -> {
            user.setUserId(userId);
            return user;
        });
    }

    @Override
    @CacheEvict(value = "telegramUserCache", key = "#user.telegramId")
    public boolean authenticateUser(LoginUser user) {
        Optional<Long> userId = userClient.findUserId(user);
        if (userId.isPresent()) {
            user.setUserId(userId.get());
            userClient.setTelegramId(user);
        }
        return userId.isPresent();
    }
}
