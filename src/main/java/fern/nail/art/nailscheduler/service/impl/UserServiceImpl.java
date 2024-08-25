package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdatePasswordDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.exception.PhoneDuplicationException;
import fern.nail.art.nailscheduler.exception.RegistrationException;
import fern.nail.art.nailscheduler.mapper.UserMapper;
import fern.nail.art.nailscheduler.model.Role;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.RoleRepository;
import fern.nail.art.nailscheduler.repository.UserRepository;
import fern.nail.art.nailscheduler.service.UserService;
import java.util.Optional;
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
    public UserResponseDto register(UserRegistrationRequestDto userRequestDto) {
        validateUsername(userRequestDto.username());
        User user = userMapper.toModel(userRequestDto);
        validatePhone(user);
        user.setRoles(Set.of(roleRepository.getByName(Role.RoleName.ROLE_CLIENT)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public boolean isMaster(User user) {
        return user.getRoles().stream()
                   .anyMatch(role -> role.getName() == Role.RoleName.ROLE_MASTER);
    }

    @Override
    public UserResponseDto getInfo(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long userId, UserUpdateRequestDto userRequestDto) {
        User user = userRepository.getReferenceById(userId);
        userMapper.updateFromDto(userRequestDto, user);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UserUpdatePasswordDto userRequestDto) {
        User user = userRepository.getReferenceById(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RegistrationException(username);
        }
    }

    private void validatePhone(User user) {
        Optional<User> optionalUser = userRepository.findByPhone(user.getPhone());
        if (optionalUser.isPresent()
                && !optionalUser.get().getId().equals(user.getId())) {
            throw new PhoneDuplicationException(user.getPhone());
        }
    }
}
