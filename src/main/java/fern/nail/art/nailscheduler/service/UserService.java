package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.user.UpdateAvgProcedureTimesDto;
import fern.nail.art.nailscheduler.dto.user.UserFullResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdatePasswordDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRequestDto);

    boolean isMaster(User user);

    UserResponseDto getInfo(Long userId);

    UserFullResponseDto getFullInfo(Long userId);

    UserResponseDto update(Long userId, UserUpdateRequestDto userRequestDto);

    void updatePassword(Long userId, UserUpdatePasswordDto userRequestDto);

    UserFullResponseDto updateAvgProcedureTimes(
            Long id, UpdateAvgProcedureTimesDto updateRequestDto);
}
