package fern.nail.art.nailscheduler.repository;

import static fern.nail.art.nailscheduler.model.UserProcedureTime.Id;

import fern.nail.art.nailscheduler.model.UserProcedureTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProcedureTimeRepository extends JpaRepository<UserProcedureTime, Id> {
}
