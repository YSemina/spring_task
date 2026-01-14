package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
