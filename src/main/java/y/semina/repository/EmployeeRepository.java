package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Employee;
import y.semina.projection.EmployeeProjection;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<EmployeeProjection> findAllProjectedBy();

    Optional<EmployeeProjection> findProjectionById(Long id);

    List<EmployeeProjection> findByDepartmentName(String departmentName);

}
