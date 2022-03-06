package pl.entre.entreweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.entre.entreweb.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
