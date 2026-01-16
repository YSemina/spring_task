package y.semina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import y.semina.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {}
