package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.exception.RegistrationException;
import fern.nail.art.nailscheduler.mapper.UserMapper;
import fern.nail.art.nailscheduler.model.Role;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.RoleRepository;
import fern.nail.art.nailscheduler.repository.UserRepository;
import fern.nail.art.nailscheduler.service.UserService;
import fern.nail.art.nailscheduler.util.PhoneNumberFormatter;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto userRequestDto) throws
            RegistrationException {

        String username = userRequestDto.username();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RegistrationException(
                    "User already exist with username \"%s\"".formatted(username));
        }

        User user = userMapper.toModel(userRequestDto);
        user.setRegisteredAt(LocalDateTime.now());
        user.setRoles(Set.of(roleRepository.getByName(Role.RoleName.ROLE_CLIENT)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPhone(PhoneNumberFormatter.getFormatter().normalize(user.getPhone()));
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
