package fern.nail.art.nailscheduler.api.dto.user;

public record UserResponseDto(
        Long id,
        String username,
        String phone,
        String firstName,
        String lastName
) {
}
