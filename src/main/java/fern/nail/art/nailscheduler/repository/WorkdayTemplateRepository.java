package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.WorkdayTemplate;
import java.time.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkdayTemplateRepository extends JpaRepository<WorkdayTemplate, DayOfWeek> {
}
