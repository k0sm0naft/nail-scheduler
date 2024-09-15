package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Workday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkdayRepository extends JpaRepository<Workday, LocalDate> {
    List<Workday> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
