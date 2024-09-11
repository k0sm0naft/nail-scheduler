package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import java.util.List;

public interface SlotService {
    Slot create(Slot slot);

    Slot update(Slot slot, Long slotId);

    Slot get(Long slotId, User user);

    List<Slot> getAllByPeriod(PeriodType periodType, int offset);

    List<Slot> getFilteredByPeriodAndProcedure(PeriodType period, int offset, User user,
            ProcedureType procedure);

    void delete(Long slotId, User user);
}
