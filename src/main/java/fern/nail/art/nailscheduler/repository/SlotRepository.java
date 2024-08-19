package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Integer> {
}
