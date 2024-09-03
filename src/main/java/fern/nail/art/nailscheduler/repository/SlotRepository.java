package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Slot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findAllByDate(LocalDate date);

    List<Slot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
