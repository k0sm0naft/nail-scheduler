package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.slot.SlotDtoFactory;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.service.SlotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/slots")
@RequiredArgsConstructor
public class ClientSlotController {
    private final SlotService slotService;
    private final SlotMapper slotMapper;
    private final SlotDtoFactory dtoFactory;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponseDto> getModifiedByPeriod(
            @RequestParam("period") PeriodType type,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam ProcedureType procedure,
            @AuthenticationPrincipal User user
    ) {
        List<Slot> slots =
                slotService.getModifiedByPeriodAndProcedure(type, offset, user.getId(), procedure);
        return slots.stream()
                    .map(slot -> dtoFactory.createDto(slot, user))
                    .toList();
    }
}
