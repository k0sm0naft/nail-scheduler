package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Appointment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllBySlotId(Long slotId);

    @EntityGraph(attributePaths = {"slot", "userProcedureTime"})
    Optional<Appointment> findById(Long id);

    @EntityGraph(attributePaths = {"slot", "userProcedureTime"})
    List<Appointment> findAll();

    @Query("FROM Appointment a JOIN FETCH a.slot JOIN FETCH a.userProcedureTime u "
            + "WHERE u.user.id = :userId")
    List<Appointment> findAllByClientIdWithSlot(Long userId);
}
