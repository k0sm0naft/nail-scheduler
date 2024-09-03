package fern.nail.art.nailscheduler.dto.user;

import fern.nail.art.nailscheduler.annotation.Name;
import fern.nail.art.nailscheduler.annotation.Phone;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @Phone
        String phone,

        @Name
        @NotBlank
        String firstName,

        @Name
        String lastName
) {
}
