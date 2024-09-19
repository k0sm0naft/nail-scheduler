package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.user.UpdateProcedureTimesDto;
import fern.nail.art.nailscheduler.dto.user.UserFullResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdatePasswordDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.mapper.UserMapper;
import fern.nail.art.nailscheduler.mapper.UserProcedureTimesMapper;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserProcedureTimesMapper procedureTimesMapper;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto get(@AuthenticationPrincipal User user) {
        user = userService.getInfo(user.getId());
        return userMapper.toDto(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserFullResponseDto get(@PathVariable Long id) {
        User user = userService.getInfo(id);
        return userMapper.toFullDto(user);
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDto updateInfo(
            @RequestBody @Valid UserUpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal User user
    ) {
        User updatedUser = userService.update(user.getId(), updateRequestDto);
        return userMapper.toDto(updatedUser);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserFullResponseDto updateClientInfo(
            @RequestBody @Valid UserUpdateRequestDto updateRequestDto,
            @PathVariable Long id) {
        User updatedUser = userService.update(id, updateRequestDto);
        return userMapper.toFullDto(updatedUser);
    }

    @PostMapping("/procedureTimes/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserFullResponseDto updateProcedureTimes(
            @RequestBody @Valid UpdateProcedureTimesDto updateRequestDto,
            @PathVariable Long id) {
        User user = userService.updateProcedureTimes(id, updateRequestDto.procedureTimes());
        return userMapper.toFullDto(user);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid UserUpdatePasswordDto updatePasswordDto,
            @AuthenticationPrincipal User user
    ) {
        userService.changePassword(user.getId(), updatePasswordDto.password());
    }
}

