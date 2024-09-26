package fern.nail.art.nailscheduler.telegram.dto;

public record UserTelegramDto(
        Long id,
        Long telegramId,
        String phone,
        String firstName,
        String lastName,
        String role
) {
}
