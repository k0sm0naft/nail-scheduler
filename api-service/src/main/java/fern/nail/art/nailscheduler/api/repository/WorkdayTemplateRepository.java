package fern.nail.art.nailscheduler.api.repository;

import fern.nail.art.nailscheduler.api.model.WorkdayTemplate;
import java.time.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkdayTemplateRepository extends JpaRepository<WorkdayTemplate, DayOfWeek> {
}
