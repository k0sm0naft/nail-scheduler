package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
