package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.user.UserLoginRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserLoginResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.exception.RegistrationException;
import fern.nail.art.nailscheduler.security.AuthenticationService;
import fern.nail.art.nailscheduler.service.UserService;
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
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody UserRegistrationRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }
}

