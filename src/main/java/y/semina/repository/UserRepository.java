package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
