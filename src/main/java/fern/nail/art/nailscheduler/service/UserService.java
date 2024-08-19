package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRequestDto)
            throws RegistrationException;
}
