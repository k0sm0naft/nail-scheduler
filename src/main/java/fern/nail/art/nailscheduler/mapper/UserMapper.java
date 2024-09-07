package fern.nail.art.nailscheduler.mapper;

import fern.nail.art.nailscheduler.config.MapperConfig;
import fern.nail.art.nailscheduler.dto.user.UserFullResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserRegistrationRequestDto;
import fern.nail.art.nailscheduler.dto.user.UserResponseDto;
import fern.nail.art.nailscheduler.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.model.User;
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
}
