package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.slot.SlotDtoFactory;
import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.PeriodType;
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
@RequestMapping(value = "/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;
    private final SlotMapper slotMapper;
    private final SlotDtoFactory dtoFactory;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public SlotResponseDto create(
            @RequestBody @Valid SlotRequestDto slotRequestDto,
            @AuthenticationPrincipal User user
    ) {
        Slot slot = slotMapper.toModel(slotRequestDto);
        slot = slotService.create(slot);
        return dtoFactory.createDto(slot, user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public SlotResponseDto update(
            @RequestBody @Valid SlotRequestDto slotRequestDto,
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Slot slot = slotMapper.toModel(slotRequestDto);
        slot = slotService.update(slot, id);
        return dtoFactory.createDto(slot, user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SlotResponseDto get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Slot slot = slotService.get(id, user);
        return dtoFactory.createDto(slot, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponseDto> getByPeriod(
            @RequestParam("period") PeriodType type,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @AuthenticationPrincipal User user
    ) {
        List<Slot> slots = slotService.getAllByPeriod(type, offset, user);
        return slots.stream()
                    .map(slot -> dtoFactory.createDto(slot, user))
                    .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        slotService.delete(id, user);
    }
}
