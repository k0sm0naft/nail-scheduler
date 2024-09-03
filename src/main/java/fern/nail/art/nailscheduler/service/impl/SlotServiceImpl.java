package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.dto.slot.SlotRequestDto;
import fern.nail.art.nailscheduler.dto.slot.SlotResponseDto;
import fern.nail.art.nailscheduler.event.SlotDeletedEvent;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotConflictException;
import fern.nail.art.nailscheduler.mapper.SlotMapper;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.SlotRepository;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.StrategyHandler;
import fern.nail.art.nailscheduler.service.UserService;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public SlotResponseDto create(SlotRequestDto slotRequestDto) {
        validateTime(slotRequestDto, slot -> true);
        Slot slot = slotMapper.toModel(slotRequestDto);
        slot.setIsAvailable(true);
        slot = slotRepository.save(slot);
        return slotMapper.toDto(slot);
    }

    @Override
    @Transactional
    public SlotResponseDto update(SlotRequestDto slotRequestDto, Long slotId) {
        validateExistence(slotId);
        validateTime(slotRequestDto, slot -> slot.getId() != slotId);
        Slot slot = slotMapper.toModel(slotRequestDto);
        Slot slotFromDb = slotRepository.getReferenceById(slotId);
        slot.setId(slotId);
        slot.setIsAvailable(slotFromDb.getIsAvailable());
        slot = slotRepository.save(slot);
        return slotMapper.toDto(slot);
    }

    @Override
    public SlotResponseDto get(Long slotId, User user) {
        Slot slot = slotRepository.findById(slotId)
                                  .filter(s -> isAccessible(user, s))
                                  .orElseThrow(
                                          () -> new EntityNotFoundException(Slot.class, slotId));
        return slotMapper.toDto(slot);
    }

    @Override
    public List<SlotResponseDto> getAllByPeriod(PeriodType periodType, int offset) {
        PeriodStrategy strategy = strategyHandler.getPeriodStrategy(periodType);
        Range range = strategy.calculateRange(offset);
        List<Slot> slots = slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
        return slots.stream()
                    .map(slotMapper::toDto)
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

    private void validateTime(SlotRequestDto slotRequestDto, Predicate<Slot> exclude) {
        LocalTime start = slotRequestDto.startTime();
        LocalTime end = slotRequestDto.endTime();
        boolean isBusy = slotRepository.findAllByDate(slotRequestDto.date()).stream()
                                       .filter(exclude)
                                       .anyMatch(slot -> start.isBefore(slot.getEndTime())
                                               && end.isAfter(slot.getStartTime()));
        if (isBusy) {
            throw new SlotConflictException(slotMapper.toModel(slotRequestDto));
        }
    }

    private boolean isAccessible(User user, Slot slot) {
        return slot.getIsPublished() || userService.isMaster(user);
    }
}
