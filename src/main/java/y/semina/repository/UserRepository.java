package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

}
