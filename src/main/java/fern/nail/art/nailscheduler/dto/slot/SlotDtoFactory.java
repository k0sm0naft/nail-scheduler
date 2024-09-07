package fern.nail.art.nailscheduler.dto.slot;

import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotDtoFactory {
    private final SlotMapper slotMapper;
    private final UserService userService;

    public SlotResponseDto createDto(Slot slot, User user) {
        if (userService.isMaster(user)) {
            return slotMapper.toMasterDto(slot);
        } else {
            return slotMapper.toPublicDto(slot);
        }
    }
}
