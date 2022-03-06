package pl.entre.entreweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.entre.entreweb.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    //User findByUserName(String username);
    // findBy + variable name (not userName if the model's field is a username)
    User findByUsername(String username);

}
