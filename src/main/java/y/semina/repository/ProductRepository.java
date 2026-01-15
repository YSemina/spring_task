package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
