package fern.nail.art.nailscheduler.api.controller;

import fern.nail.art.nailscheduler.api.dto.user.UserLoginRequestDto;
import fern.nail.art.nailscheduler.api.dto.user.UserLoginResponseDto;
import fern.nail.art.nailscheduler.api.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.api.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.api.exception.RegistrationException;
import fern.nail.art.nailscheduler.api.mapper.UserMapper;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.security.AuthenticationService;
import fern.nail.art.nailscheduler.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto userRequestDto)
            throws RegistrationException {
        User user = userMapper.toModel(userRequestDto);
        user = userService.register(user);
        return userMapper.toDto(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }
}

