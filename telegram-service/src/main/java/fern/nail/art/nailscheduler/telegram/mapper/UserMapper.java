package fern.nail.art.nailscheduler.telegram.mapper;

import fern.nail.art.nailscheduler.telegram.dto.UserTelegramDto;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UserMapper {

    public Optional<User> dtoToUser(UserTelegramDto dto) {
        if (dto.telegramId() == null) {
            return Optional.empty();
        }

        return Optional.of(User.builder()
                               .userId(dto.id())
                               .telegramId(dto.telegramId())
                               .firstName(dto.firstName())
                               .lastName(dto.lastName())
                               .role(mapToRole(dto.role()))
                               .phone(dto.phone())
                               .build());
    }

    public User userFromUpdate(Update update) {
        org.telegram.telegrambots.meta.api.objects.User telegramUser = AbilityUtils.getUser(update);
        return User.builder()
                   .telegramId(telegramUser.getId())
                   .firstName(telegramUser.getFirstName())
                   .lastName(telegramUser.getLastName())
                   .locale(Locale.forLanguageTag(telegramUser.getLanguageCode()))
                   .role(User.Role.UNKNOWN)
                   .globalState(GlobalState.IDLE)
                   .build();
    }

    private User.Role mapToRole(String role) {
        if (role == null) {
            return User.Role.UNKNOWN;
        }

        return switch (role) {
            case "CLIENT" -> User.Role.CLIENT;
            case "MASTER" -> User.Role.MASTER;
            default -> User.Role.UNKNOWN;
        };
    }
}
