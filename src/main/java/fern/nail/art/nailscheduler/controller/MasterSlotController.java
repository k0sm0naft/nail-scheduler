package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.slot.SlotDtoFactory;
import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.service.SlotService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/masters/slots")
@PreAuthorize("hasRole('ROLE_MASTER')")
@RequiredArgsConstructor
public class MasterSlotController {
    private final SlotService slotService;
    private final SlotMapper slotMapper;
    private final SlotDtoFactory dtoFactory;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotResponseDto create(
            @RequestBody @Valid SlotRequestDto slotRequestDto,
            @AuthenticationPrincipal User user
    ) {
        Slot slot = slotMapper.toModel(slotRequestDto, null);
        slot = slotService.createOrUpdate(slot);
        return dtoFactory.createDto(slot, user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SlotResponseDto update(
            @RequestBody @Valid SlotRequestDto slotRequestDto,
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Slot slot = slotMapper.toModel(slotRequestDto, id);
        slot = slotService.createOrUpdate(slot);
        return dtoFactory.createDto(slot, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponseDto> getByPeriod(
            @RequestParam PeriodType period,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @AuthenticationPrincipal User user
    ) {
        List<Slot> slots = slotService.getAllByPeriod(period, offset);
        return slots.stream()
                    .map((Slot slot) -> dtoFactory.createDto(slot, user))
                    .toList();
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponseDto> getForUserByPeriod(
            @PathVariable Long id,
            @RequestParam("period") PeriodType period,
            @RequestParam("procedure") ProcedureType procedure,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @AuthenticationPrincipal User user
    ) {
        List<Slot> slots =
                slotService.getModifiedByPeriodAndProcedure(period, offset, id, procedure);
        return slots.stream()
                    .map(slot -> dtoFactory.createDto(slot, user))
                    .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        slotService.delete(id, user);
    }
}
