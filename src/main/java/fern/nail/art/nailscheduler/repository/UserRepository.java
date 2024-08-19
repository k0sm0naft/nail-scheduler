package fern.nail.art.nailscheduler.repository;

import fern.nail.art.nailscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
