package fern.nail.art.nailscheduler.api.repository;

import fern.nail.art.nailscheduler.api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @Query("FROM User u JOIN FETCH u.procedureTimes WHERE u.id = :id")
    Optional<User> findByIdWithProcedureTimes(Long id);

    Optional<User> findByPhone(String phone);
}
