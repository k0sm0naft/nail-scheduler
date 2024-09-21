package fern.nail.art.nailscheduler.api.repository;

import static fern.nail.art.nailscheduler.api.model.UserProcedureTime.Id;

import fern.nail.art.nailscheduler.api.model.UserProcedureTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProcedureTimeRepository extends JpaRepository<UserProcedureTime, Id> {
}
