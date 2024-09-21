package fern.nail.art.nailscheduler.api.repository;

import fern.nail.art.nailscheduler.api.model.Slot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    @Query("FROM Slot s "
            + "LEFT JOIN FETCH s.appointment a "
            + "LEFT JOIN FETCH a.userProcedureTime "
            + "WHERE s.id = :id")
    Optional<Slot> findByIdWithAppointments(Long id);

    @EntityGraph(attributePaths = "appointment.userProcedureTime")
    List<Slot> findAllByDate(LocalDate date);

    @EntityGraph(attributePaths = "appointment.userProcedureTime")
    List<Slot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("FROM Slot s "
            + "LEFT JOIN FETCH s.appointment a "
            + "LEFT JOIN FETCH a.userProcedureTime "
            + "WHERE s.date = (SELECT s2.date FROM Slot s2 WHERE s2.id = :id)")
    List<Slot> findAllOnSameDayAsSlotId(Long id);

    @Modifying
    @Query("DELETE FROM Slot s "
            + "WHERE s.date < :date AND s.appointment IS NULL")
    void deleteEmptyByDateBefore(LocalDate date);

    void deleteAllByDate(LocalDate date);
}
