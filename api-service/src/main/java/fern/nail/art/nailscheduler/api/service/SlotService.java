package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.Slot;
import fern.nail.art.nailscheduler.api.model.User;
import java.time.LocalDate;
import java.util.List;

public interface SlotService {
    Slot createOrUpdate(Slot slot);

    void generateSlotsForDay(LocalDate date);

    Slot getModified(Long slotId, int procedureDuration);

    List<Slot> getAllByPeriod(PeriodType periodType, int offset);

    List<Slot> getModifiedByPeriodAndProcedure(PeriodType period, int offset, Long userId,
            ProcedureType procedure);

    void delete(Long slotId, User user);

    void deleteEmptyBefore(LocalDate date);

    void deleteAllByDate(LocalDate date);
}
