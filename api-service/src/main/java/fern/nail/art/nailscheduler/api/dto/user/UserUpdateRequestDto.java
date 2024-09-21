package fern.nail.art.nailscheduler.api.dto.user;

import fern.nail.art.nailscheduler.api.annotation.Name;
import fern.nail.art.nailscheduler.api.annotation.Phone;
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
