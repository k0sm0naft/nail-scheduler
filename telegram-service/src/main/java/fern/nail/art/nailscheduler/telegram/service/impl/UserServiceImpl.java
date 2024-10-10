package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.UserClient;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.RegistrationResult;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
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
    private final MessageService messageService;

    @Override
    @Cacheable(value = "telegramUserCache", key = "T(org.telegram.telegrambots.abilitybots.api.util.AbilityUtils).getChatId(#update)")
    public User getUser(Update update) {
        //todo add real db for telegramUsers
        //todo try get user from db, next try get from api, next create tempUser for reg-on or login
        return userClient.findUserByTelegramId(AbilityUtils.getChatId(update))
                         .orElse(userMapper.userFromUpdate(update));
    }

    @Override
    @CachePut(value = "telegramUserCache", key = "#user.telegramId")
    public User saveTempUser(User user) {
        return user;
    }

    @Override
    @Cacheable(value = "telegramUserCache", key = "#user.telegramId")
    public User getTempUser(User user) {
        return user;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "telegramUserCache", key = "#user.telegramId"),
            @CacheEvict(value = "telegramNewUserCache", key = "#user.telegramId")
    })
    public void deleteTempUser(User user) {
    }

    @Override
    public RegistrationResult register(AuthUser user) {
        return userClient.registerUser(user);
    }

    @Override
    @CacheEvict(value = "telegramUserCache", key = "#user.telegramId")
    public boolean authenticate(AuthUser user) {
        Optional<Long> userId = userClient.findUserId(user);
        if (userId.isPresent()) {
            user.setUserId(userId.get());
            userClient.setTelegramId(user);
            user.setRole(User.Role.CLIENT);
        }
        return userId.isPresent();
    }
}
