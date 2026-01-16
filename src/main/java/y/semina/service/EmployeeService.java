package y.semina.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import y.semina.model.Employee;
import y.semina.projection.EmployeeProjection;
import y.semina.repository.EmployeeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeProjection> findAllProjections() {
        return employeeRepository.findAllProjectedBy();
    }

    public EmployeeProjection findProjectionById(Long id) {
        return employeeRepository.findProjectionById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
    }

    public List<EmployeeProjection> findProjectionsByDepartment(String departmentName) {
        return employeeRepository.findByDepartmentName(departmentName);
    }

}
