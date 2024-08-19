package fern.nail.art.nailscheduler.dto.user;

public record UserRegistrationRequestDto(
        String username,
        String password,
        String repeatPassword,
        String phone,
        String firstName,
        String lastName
) {
}
