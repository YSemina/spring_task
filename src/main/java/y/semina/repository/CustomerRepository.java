package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserUsername(String username);

}
