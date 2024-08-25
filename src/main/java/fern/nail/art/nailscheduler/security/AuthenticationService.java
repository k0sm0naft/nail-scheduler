package fern.nail.art.nailscheduler.security;

import fern.nail.art.nailscheduler.dto.user.UserLoginRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserLoginResponseDto;
import fern.nail.art.nailscheduler.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.username(), userLoginRequestDto.password()));
        String generatedToken = jwtUtil.generateToken((User) authentication.getPrincipal());
        return new UserLoginResponseDto(generatedToken);
    }
}
