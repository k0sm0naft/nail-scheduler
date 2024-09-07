package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.PhoneDuplicationException;
import fern.nail.art.nailscheduler.exception.RegistrationException;
import fern.nail.art.nailscheduler.mapper.UserMapper;
import fern.nail.art.nailscheduler.model.Role;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.RoleRepository;
import fern.nail.art.nailscheduler.repository.UserRepository;
import fern.nail.art.nailscheduler.service.UserProcedureTimeService;
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
    private final UserProcedureTimeService procedureTimeService;

    @Override
    @Transactional
    public User register(User user) {
        validateUsername(user.getUsername());
        validatePhone(user);
        user.setRoles(Set.of(roleRepository.getByName(Role.RoleName.ROLE_CLIENT)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProcedureTimes(procedureTimeService.getDefault(user));
        return userRepository.save(user);
    }

    @Override
    public boolean isMaster(User user) {
        return user.getRoles().stream()
                   .anyMatch(role -> role.getName() == Role.RoleName.ROLE_MASTER);
    }

    @Override
    public User getInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public User getFullInfo(Long userId) {
        return userRepository.findByIdWithProcedureTimes(userId)
                             .orElseThrow(
                                     () -> new EntityNotFoundException(User.class, userId));
    }

    @Override
    @Transactional
    public User update(Long userId, UserUpdateRequestDto requestDto) {
        User user = getById(userId);
        userMapper.updateFromDto(requestDto, user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateProcedureTimes(Long id, Set<ProcedureTimeDto> procedureTimes) {
        User user = getById(id);
        procedureTimeService.setToUser(procedureTimes, user);
        return userRepository.save(user);
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException(User.class, id));
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
