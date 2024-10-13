package fern.nail.art.nailscheduler.telegram.service.impl;

import fern.nail.art.nailscheduler.telegram.client.UserClient;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.RegistrationResult;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "telegramUserCache",
            key = "T(org.telegram.telegrambots.abilitybots.api.util.AbilityUtils)"
                    + ".getChatId(#update)")
    public User getUser(Update update) {
        //todo add real db for telegramUsers
        //todo try get user from db, next try get from api, next create tempUser for reg-on or login
        return userClient.findUserByTelegramId(AbilityUtils.getChatId(update))
                         .orElse(userMapper.userFromUpdate(update));
    }

    @Override
    @CachePut(value = "telegramUserCache", key = "#user.telegramId")
    public User saveUser(User user) {
        return user;
    }

    @Override
    public RegistrationResult register(AuthUser user) {
        return userClient.registerUser(user);
    }

    @Override
    @CacheEvict(value = "telegramUserCache", key = "#user.telegramId")
    public boolean authenticate(AuthUser user) {
        Optional<Long> userId = userClient.findUserId(user);
        user.setLocalState(null);
        if (userId.isPresent()) {
            user.setGlobalState(GlobalState.IDLE);
            user.setUserId(userId.get());
            userClient.setTelegramId(user);
            user.setRole(User.Role.CLIENT);
        } else {
            user.setGlobalState(GlobalState.AUTHENTICATION);
        }
        return userId.isPresent();
    }
}
