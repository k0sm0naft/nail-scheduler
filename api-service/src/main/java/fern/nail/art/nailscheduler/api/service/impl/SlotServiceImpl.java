package fern.nail.art.nailscheduler.api.service.impl;

import static fern.nail.art.nailscheduler.api.model.Slot.Status.PUBLISHED;

import fern.nail.art.nailscheduler.api.event.SlotDeletedEvent;
import fern.nail.art.nailscheduler.api.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.api.exception.SlotConflictException;
import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.Range;
import fern.nail.art.nailscheduler.api.model.Slot;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.repository.SlotRepository;
import fern.nail.art.nailscheduler.api.service.PeriodStrategyHandler;
import fern.nail.art.nailscheduler.api.service.ScheduleManager;
import fern.nail.art.nailscheduler.api.service.SlotService;
import fern.nail.art.nailscheduler.api.service.UserProcedureTimeService;
import fern.nail.art.nailscheduler.api.strategy.period.PeriodStrategy;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PeriodStrategyHandler periodStrategyHandler;
    private final ScheduleManager scheduleManager;
    private final UserProcedureTimeService procedureTimeService;
    @Value("${duration.min.procedure}")
    private Integer minSlotTime;
    private int slotsInADay = 5;
    private int intervalHours = 2;
    private LocalTime firstSlotTime = LocalTime.of(9, 0);

    @Override
    @Transactional
    @CacheEvict(value = "slotCache", allEntries = true)
    public Slot createOrUpdate(Slot slot) {
        validateTime(slot);
        return slotRepository.save(slot);
    }

    @Override
    @Transactional
    @CacheEvict(value = "slotCache", allEntries = true)
    public void generateSlotsForDay(LocalDate date) {
        List<Slot> daySlots = new ArrayList<>(slotsInADay);
        LocalTime startTime = firstSlotTime;

        for (int i = 0; i < slotsInADay; i++) {
            Slot slot = new Slot();
            slot.setStatus(PUBLISHED);
            slot.setDate(date);
            slot.setStartTime(startTime);
            startTime = startTime.plusHours(intervalHours);
            daySlots.add(slot);
        }

        slotRepository.saveAll(daySlots);
    }

    @Override
    public Slot getModified(Long slotId, int procedureDuration) {
        List<Slot> slotsByDay = slotRepository.findAllOnSameDayAsSlotId(slotId);

        if (slotsByDay.isEmpty()) {
            throw new EntityNotFoundException(Slot.class, slotId);
        }

        LocalDate date = slotsByDay.getLast().getDate();
        Range range = new Range(date, date);

        List<Slot> modifiedSlots =
                scheduleManager.getModifiedSlots(slotsByDay, procedureDuration, range);
        return modifiedSlots.stream()
                .filter(slot -> slot.getId().equals(slotId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Slot.class, slotId));
    }

    @Override
    @Cacheable(value = "slotCache")
    public List<Slot> getAllByPeriod(PeriodType period, int offset) {
        Range range = getRange(period, offset);
        return slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
    }

    @Override
    public List<Slot> getModifiedByPeriodAndProcedure(PeriodType period, int offset, Long userId,
            ProcedureType procedure) {
        SlotService proxy = (SlotService) AopContext.currentProxy();
        List<Slot> slots = proxy.getAllByPeriod(period, offset);
        int procedureDuration = procedureTimeService.get(procedure, userId).getDuration();
        return scheduleManager.getModifiedSlots(slots, procedureDuration, getRange(period, offset));
    }

    @Override
    @Transactional
    @CacheEvict(value = "slotCache", allEntries = true)
    public void delete(Long slotId, User user) {
        validateExistence(slotId);
        eventPublisher.publishEvent(new SlotDeletedEvent(slotId, user));
        slotRepository.deleteById(slotId);
    }

    @Override
    @Transactional
    public void deleteEmptyBefore(LocalDate date) {
        slotRepository.deleteEmptyByDateBefore(date);
    }

    @Override
    @CacheEvict(value = "slotCache", allEntries = true)
    public void deleteAllByDate(LocalDate date) {
        slotRepository.deleteAllByDate(date);
    }

    private Range getRange(PeriodType period, int offset) {
        PeriodStrategy strategy = periodStrategyHandler.getPeriodStrategy(period);
        return strategy.calculateRange(offset);
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
}
