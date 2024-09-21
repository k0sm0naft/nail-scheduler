package fern.nail.art.nailscheduler.api.repository;

import fern.nail.art.nailscheduler.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByName(Role.RoleName name);
}
