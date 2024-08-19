package fern.nail.art.nailscheduler.dto.user;

public record UserResponseDto(
        Long id,
        String username,
        String phone,
        String firstName,
        String lastName
) {
}
