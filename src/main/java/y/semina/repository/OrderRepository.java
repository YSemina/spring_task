package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
