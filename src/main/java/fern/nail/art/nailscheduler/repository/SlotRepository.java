package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Slot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    @Query("FROM Slot s JOIN FETCH s.appointment a JOIN FETCH a.userProcedureTime WHERE s.id = :id")
    Optional<Slot> findByIdWithAppointments(Long id);

    @EntityGraph(attributePaths = "appointment.userProcedureTime")
    List<Slot> findAllByDate(LocalDate date);

    @EntityGraph(attributePaths = "appointment.userProcedureTime")
    List<Slot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
