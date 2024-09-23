package fern.nail.art.nailscheduler.telegram.mapper;

import fern.nail.art.nailscheduler.telegram.dto.UserTelegramRequestDto;
import fern.nail.art.nailscheduler.telegram.model.User;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Optional<User> dtoToUser(UserTelegramRequestDto dto) {
        if (dto == null) {
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

    public User telegramUserToUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        return User.builder()
                   .telegramId(telegramUser.getId())
                   .firstName(telegramUser.getFirstName())
                   .lastName(telegramUser.getLastName())
                   .locale(Locale.forLanguageTag(telegramUser.getLanguageCode()))
                   .build();
    }


    private static User.Role mapToRole(String role) {
        return switch (role.toUpperCase()) {
            case "CLIENT" -> User.Role.CLIENT;
            case "MASTER" -> User.Role.MASTER;
            default -> User.Role.UNKNOWN;
        };
    }
}
