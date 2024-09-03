package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.User;
import java.util.List;

public interface SlotService {
    SlotResponseDto create(SlotRequestDto slotRequestDto);

    SlotResponseDto update(SlotRequestDto slotRequestDto, Long slotId);

    SlotResponseDto get(Long slotId, User user);

    List<SlotResponseDto> getAllByPeriod(PeriodType periodType, int offset);

    void delete(Long slotId, User user);
}
