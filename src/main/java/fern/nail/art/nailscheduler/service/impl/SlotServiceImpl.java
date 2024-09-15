package fern.nail.art.nailscheduler.service.impl;

import static fern.nail.art.nailscheduler.model.Slot.Status.PUBLISHED;

import fern.nail.art.nailscheduler.event.SlotDeletedEvent;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotConflictException;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.SlotRepository;
import fern.nail.art.nailscheduler.service.ScheduleManager;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.StrategyHandler;
import fern.nail.art.nailscheduler.service.UserProcedureTimeService;
import fern.nail.art.nailscheduler.service.UserService;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final StrategyHandler strategyHandler;
    private final ScheduleManager scheduleManager;
    private final UserProcedureTimeService procedureTimeService;
    @Value("${duration.min.procedure}")
    private Integer minSlotTime;
    private int slotsInADay = 5;
    private int intervalHours = 2;
    private LocalTime firstSlotTime = LocalTime.of(9, 0);

    @Override
    @Transactional
    public Slot create(Slot slot) {
        validateTime(slot);
        return slotRepository.save(slot);
    }

    @Override
    @Transactional
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
    @Transactional
    public Slot update(Slot slot, Long slotId) {
        validateExistence(slotId);
        validateTime(slot);
        slot.setId(slotId);
        return slotRepository.save(slot);
    }

    @Override
    public Slot getModified(Long slotId, Long userId, ProcedureType procedure) {
        List<Slot> slotsByDay = slotRepository.findAllOnSameDayAsSlotId(slotId);
        int procedureDuration = procedureTimeService.get(procedure, userId).getDuration();

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
    public List<Slot> getAllByPeriod(PeriodType period, int offset) {
        Range range = getRange(period, offset);
        return slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
    }

    @Override
    public List<Slot> getModifiedByPeriodAndProcedure(PeriodType period, int offset, Long userId,
            ProcedureType procedure) {
        Range range = getRange(period, offset);
        List<Slot> slots = slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
        int procedureDuration = procedureTimeService.get(procedure, userId).getDuration();
        return scheduleManager.getModifiedSlots(slots, procedureDuration, range);
    }

    @Override
    @Transactional
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
    public void deleteAllByDate(LocalDate date) {
        slotRepository.deleteAllByDate(date);
    }

    private Range getRange(PeriodType period, int offset) {
        PeriodStrategy strategy = strategyHandler.getPeriodStrategy(period);
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

    private boolean isAccessible(User user, Slot slot) {
        return slot.getStatus().equals(PUBLISHED) || userService.isMaster(user);
    }

    private Slot getSlot(Long slotId, User user) {
        return slotRepository.findByIdWithAppointments(slotId)
                             .filter(s -> isAccessible(user, s))
                             .orElseThrow(() -> new EntityNotFoundException(Slot.class, slotId));
    }
}
