package fern.nail.art.nailscheduler.telegram.mapper;

import fern.nail.art.nailscheduler.telegram.dto.UserTelegramDto;
import fern.nail.art.nailscheduler.telegram.model.User;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;
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
                               .firstName(normalizeName(dto.firstName()))
                               .lastName(normalizeName(dto.lastName()))
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

    public User createTempUser(User user, Update update) {
        user.setRole(User.Role.UNKNOWN);
        return user;
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

    private String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        String cleanedName = name.replaceAll("[^a-zA-Zа-яА-Я]", "");

        if (cleanedName.length() < 3) {
            cleanedName = String.format("%-3s", cleanedName).replace(' ', 'a');
        } else if (cleanedName.length() > 24) {
            cleanedName = cleanedName.substring(0, 24);
        }

        return cleanedName.substring(0, 1).toUpperCase() + cleanedName.substring(1).toLowerCase();
    }
}
