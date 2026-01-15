package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
