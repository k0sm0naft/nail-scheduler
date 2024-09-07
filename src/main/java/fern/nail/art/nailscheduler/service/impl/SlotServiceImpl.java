package fern.nail.art.nailscheduler.service.impl;

import static fern.nail.art.nailscheduler.model.Slot.Status.PUBLISHED;
import static fern.nail.art.nailscheduler.model.Slot.Status.SHIFTED;
import static fern.nail.art.nailscheduler.model.Slot.Status.UNPUBLISHED;

import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotConflictException;
import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.SlotDeletedEvent;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.SlotRepository;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.StrategyHandler;
import fern.nail.art.nailscheduler.service.UserService;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final StrategyHandler strategyHandler;
    @Value("${duration.min.procedure}")
    private Integer minSlotTime;

    @Override
    @Transactional
    public SlotResponseDto create(SlotRequestDto slotRequestDto) {
        Slot slot = slotMapper.toModel(slotRequestDto);
        validateTime(slot);
        slot = slotRepository.save(slot);
        return slotMapper.toMasterDto(slot);
    }

    @Override
    @Transactional
    public SlotResponseDto update(SlotRequestDto slotRequestDto, Long slotId) {
        validateExistence(slotId);
        Slot slot = slotMapper.toModel(slotRequestDto);
        slot.setId(slotId);
        validateTime(slot);
        slot.setStatus(slotRequestDto.isPublished() ? PUBLISHED : UNPUBLISHED);
        slot = slotRepository.save(slot);
        return slotMapper.toMasterDto(slot);
    }

    @Override
    public SlotResponseDto get(Long slotId, User user) {
        Slot slot = getSlot(slotId, user);
        return getSlotResponseDto(user, slot);
    }

    @Override
    public Slot get(User user, Long slotId) {
        return getSlot(slotId, user);
    }

    @Override
    public List<SlotResponseDto> getAllByPeriod(PeriodType periodType, int offset, User user) {
        PeriodStrategy strategy = strategyHandler.getPeriodStrategy(periodType);
        Range range = strategy.calculateRange(offset);
        List<Slot> slots = slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
        return slots.stream()
                    .map(slot -> getSlotResponseDto(user, slot))
                    .toList();
    }

    @Override
    @Transactional
    public void delete(Long slotId, User user) {
        validateExistence(slotId);
        eventPublisher.publishEvent(new SlotDeletedEvent(slotId, user));
        slotRepository.deleteById(slotId);
    }

    private void validateExistence(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new EntityNotFoundException(Slot.class, slotId);
        }
    }

    private void validateTime(Slot slot) {
        LocalTime start = slot.getStartTime();
        LocalTime end = start.plusMinutes(minSlotTime);
        boolean isBusy = slotRepository.findAllByDate(slot.getDate()).stream()
                                       .filter(s -> !Objects.equals(s.getId(), slot.getId()))
                                       .anyMatch(s -> hasConflict(s, start, end));
        if (isBusy) {
            throw new SlotConflictException(slot);
        }
    }

    private boolean hasConflict(Slot s, LocalTime start, LocalTime end) {
        return start.isBefore(getSlotEndTime(s))
                && end.isAfter(s.getStartTime());
    }

    private LocalTime getSlotEndTime(Slot slot) {
        Integer minutesToAdd;
        if (slot.getAppointment() == null) {
            minutesToAdd = minSlotTime;
        } else {
            minutesToAdd = slot.getAppointment().getUserProcedureTime().getDuration();
        }
        return slot.getStartTime().plusMinutes(minutesToAdd);
    }

    private boolean isAccessible(User user, Slot slot) {
        return slot.getStatus().equals(PUBLISHED)
                || slot.getStatus().equals(SHIFTED)
                || userService.isMaster(user);
    }

    private Slot getSlot(Long slotId, User user) {
        return slotRepository.findByIdWithAppointments(slotId)
                             .filter(s -> isAccessible(user, s))
                             .orElseThrow(
                                     () -> new EntityNotFoundException(Slot.class, slotId));
    }

    private SlotResponseDto getSlotResponseDto(User user, Slot slot) {
        if (userService.isMaster(user)) {
            return slotMapper.toMasterDto(slot);
        }
        return slotMapper.toPublicDto(slot);
    }
}
