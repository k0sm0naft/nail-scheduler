package fern.nail.art.nailscheduler.service.impl;

import static fern.nail.art.nailscheduler.model.Slot.Status.PUBLISHED;
import static fern.nail.art.nailscheduler.model.Slot.Status.SHIFTED;

import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotConflictException;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.SlotDeletedEvent;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import fern.nail.art.nailscheduler.repository.SlotRepository;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.SlotShiftingManager;
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
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final StrategyHandler strategyHandler;
    private final SlotShiftingManager slotShiftingManager;
    @Value("${duration.avg.manicure}")
    private Integer defaultManicureTime;
    @Value("${duration.avg.pedicure}")
    private Integer defaultPedicureTime;
    @Value("${duration.min.procedure}")
    private Integer minSlotTime;

    // todo add scheduled method for cleaning empty slots from the past.
    @Override
    @Transactional
    public Slot create(Slot slot) {
        validateTime(slot);
        return slotRepository.save(slot);
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
        int procedureDuration = getProcedureDuration(userId, procedure);
        List<Slot> modifiedSlots =
                slotShiftingManager.getModifiedSlots(slotsByDay, procedureDuration);
        return modifiedSlots.stream()
                .filter(slot -> slot.getId().equals(slotId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Slot.class, slotId));
    }

    @Override
    public List<Slot> getAllByPeriod(PeriodType period, int offset) {
        PeriodStrategy strategy = strategyHandler.getPeriodStrategy(period);
        Range range = strategy.calculateRange(offset);
        return slotRepository.findAllByDateBetween(range.startDate(), range.endDate());
    }

    @Override
    public List<Slot> getModifiedByPeriodAndProcedure(PeriodType period, int offset, Long userId,
            ProcedureType procedure) {
        List<Slot> slots = getAllByPeriod(period, offset);
        int procedureDuration = getProcedureDuration(userId, procedure);
        return slotShiftingManager.getModifiedSlots(slots, procedureDuration);
    }

    private int getProcedureDuration(Long userId, ProcedureType procedure) {
        return userService.getFullInfo(userId).getProcedureTimes().stream()
                          .filter(upt -> upt.getId().getProcedure().equals(procedure))
                          .mapToInt(UserProcedureTime::getDuration)
                          .findFirst()
                          .orElseGet(() -> procedure.equals(ProcedureType.MANICURE)
                                  ? defaultManicureTime : defaultPedicureTime);
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
                             .orElseThrow(() -> new EntityNotFoundException(Slot.class, slotId));
    }
}
