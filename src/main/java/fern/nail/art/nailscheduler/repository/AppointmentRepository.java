package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Appointment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllBySlotId(Long slotId);

    @Query("FROM Appointment a JOIN FETCH a.slot WHERE a.id = :slotId")
    Optional<Appointment> findByIdWithSlots(Long slotId);

    @Query("FROM Appointment a JOIN FETCH a.slot")
    List<Appointment> findAllWithSlots();

    @Query("FROM Appointment a JOIN FETCH a.slot WHERE a.clientId = :clientId")
    List<Appointment> findAllByClientIdWithSlot(Long clientId);
}
