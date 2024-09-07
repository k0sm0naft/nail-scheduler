package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.user.UpdateProcedureTimesDto;
import fern.nail.art.nailscheduler.dto.user.UserFullResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdatePasswordDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
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

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto get(@AuthenticationPrincipal User user) {
        return userService.getInfo(user.getId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserFullResponseDto get(@PathVariable Long id) {
        return userService.getFullInfo(id);
    }

    @PostMapping("/update/me")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDto updateInfo(
            @RequestBody @Valid UserUpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal User user
    ) {
        return userService.update(user.getId(), updateRequestDto);
    }

    @PostMapping("/update/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserResponseDto updateClientInfo(
            @RequestBody @Valid UserUpdateRequestDto updateRequestDto,
            @PathVariable Long id) {
        return userService.update(id, updateRequestDto);
    }

    @PostMapping("/procedureTimes/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public UserFullResponseDto updateProcedureTimes(
            @RequestBody @Valid UpdateProcedureTimesDto updateRequestDto,
            @PathVariable Long id) {
        return userService.updateProcedureTimes(id, updateRequestDto);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid UserUpdatePasswordDto updatePasswordDto,
            @AuthenticationPrincipal User user
    ) {
        userService.changePassword(user.getId(), updatePasswordDto);
    }
}

