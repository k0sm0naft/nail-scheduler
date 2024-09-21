package fern.nail.art.nailscheduler.api.dto.slot;

import fern.nail.art.nailscheduler.api.mapper.SlotMapper;
import fern.nail.art.nailscheduler.api.model.Slot;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotDtoFactory {
    private final SlotMapper slotMapper;
    private final UserService userService;

    public SlotResponseDto createDto(Slot slot, User user) {
        if (userService.isMaster(user)) {
            if (slot.getAppointment() == null) {
                return slotMapper.toFreeMasterDto(slot);
            }
            return slotMapper.toBusyMasterDto(slot);
        } else {
            return slotMapper.toPublicDto(slot);
        }
    }
}
