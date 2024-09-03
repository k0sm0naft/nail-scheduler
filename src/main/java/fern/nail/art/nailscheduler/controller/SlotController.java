package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.model.PeriodType;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public SlotResponseDto create(@RequestBody @Valid SlotRequestDto slotRequestDto) {
        return slotService.create(slotRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public SlotResponseDto update(
            @RequestBody @Valid SlotRequestDto slotRequestDto,
            @PathVariable Long id
    ) {
        return slotService.update(slotRequestDto, id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SlotResponseDto get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return slotService.get(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponseDto> getByPeriod(
            @RequestParam("periodType") PeriodType type,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset) {
        return slotService.getAllByPeriod(type, offset);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        slotService.delete(id, user);
    }
}
