package fern.nail.art.nailscheduler.api.dto.user;

public record UserTelegramResponseDto(
        Long id,
        String telegramId,
        String phone,
        String firstName,
        String lastName,
        String role
) {
}
