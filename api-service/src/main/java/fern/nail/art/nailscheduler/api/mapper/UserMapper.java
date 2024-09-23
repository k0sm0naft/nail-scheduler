package fern.nail.art.nailscheduler.api.mapper;

import fern.nail.art.nailscheduler.api.config.MapperConfig;
import fern.nail.art.nailscheduler.api.dto.user.UserFullResponseDto;
import fern.nail.art.nailscheduler.api.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.api.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.api.dto.user.UserTelegramResponseDto;
import fern.nail.art.nailscheduler.api.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.api.model.Role;
import fern.nail.art.nailscheduler.api.model.User;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = UserProcedureTimesMapper.class)
public interface UserMapper {
    String UKR_LOCAL_NUMBER = "38";
    String PLUS = "+";

    UserResponseDto toDto(User user);

    UserFullResponseDto toFullDto(User user);

    @Mapping(target = "role", source = "roles", qualifiedByName = "getTelegramRole")
    UserTelegramResponseDto toTelegramDto(User user);

    @Mapping(target = "phone", source = "phone", qualifiedByName = "normalizePhone")
    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "phone", source = "phone", qualifiedByName = "normalizePhone")
    void updateFromDto(UserUpdateRequestDto userRequest, @MappingTarget User user);

    @Named("normalizePhone")
    default String normalizePhone(String phoneNumber) {
        String numericOnly = phoneNumber.replaceAll("\\D", "");
        if (numericOnly.length() == 10) {
            numericOnly = UKR_LOCAL_NUMBER + numericOnly;
        }
        if (numericOnly.length() == 12 && numericOnly.startsWith(UKR_LOCAL_NUMBER)) {
            return PLUS + numericOnly;
        }
        throw new IllegalArgumentException("Invalid phone number format");
    }

    @Named("getTelegramRole")
    default String getTelegramRole(Set<Role> roles) {
        return roles.stream()
                    .filter(role -> role.getName().equals(Role.RoleName.ROLE_MASTER))
                    .findFirst()
                    .map(Role::getName)
                    .orElse(Role.RoleName.ROLE_CLIENT)
                    .toString()
                    .replace("ROLE_", "");
    }
}
