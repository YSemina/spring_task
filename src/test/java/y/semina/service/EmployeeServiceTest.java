package y.semina.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import y.semina.model.Employee;
import y.semina.projection.EmployeeProjection;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    @Order(1)
    void findAllProjections_ShouldReturn10Employees() {
        List<EmployeeProjection> projections = employeeService.findAllProjections();

        assertEquals(10, projections.size());

        for (EmployeeProjection p : projections) {
            assertNotNull(p.getFullName());
            assertNotNull(p.getPosition());
            assertNotNull(p.getDepartmentName());
        }
    }

    @Test
    @Order(2)
    void findProjectionById_ShouldReturnCorrectEmployee() {
        EmployeeProjection dmitriy = employeeService.findProjectionById(8L);

        assertEquals("Дмитрий Новиков", dmitriy.getFullName());
        assertEquals("Financial Analyst", dmitriy.getPosition());
        assertEquals("Finance Department", dmitriy.getDepartmentName());
    }

    @Test
    @Order(3)
    void findProjectionsByDepartment_IT_Department_ShouldReturn3Employees() {
        List<EmployeeProjection> itEmployees = employeeService.findProjectionsByDepartment("IT Department");

        assertEquals(3, itEmployees.size());

        List<String> expectedFullNames = Arrays.asList(
                "Иван Иванов",
                "Петр Петров",
                "Сергей Сидоров"
        );

        List<String> actualFullNames = itEmployees.stream()
                .map(EmployeeProjection::getFullName)
                .toList();

        assertTrue(actualFullNames.containsAll(expectedFullNames));

        assertEquals("Senior Java Developer", itEmployees.get(0).getPosition());
        assertEquals("Frontend Developer", itEmployees.get(1).getPosition());
        assertEquals("DevOps Engineer", itEmployees.get(2).getPosition());

        for (EmployeeProjection emp : itEmployees) {
            assertEquals("IT Department", emp.getDepartmentName());
        }
    }

    @Test
    @Order(4)
    void testAllMethodsFromProjectionInterface() {
        EmployeeProjection projection = employeeService.findProjectionById(1L);

        assertNotNull(projection.getPosition());
        assertEquals("Senior Java Developer", projection.getPosition());

        assertNotNull(projection.getDepartmentName());
        assertEquals("IT Department", projection.getDepartmentName());

        assertNotNull(projection.getFullName());
        assertEquals("Иван Иванов", projection.getFullName());

        System.out.println("getFullName(): " + projection.getFullName());
        System.out.println("getPosition(): " + projection.getPosition());
        System.out.println("getDepartmentName(): " + projection.getDepartmentName());
    }

    @Test
    @Order(5)
    void findProjectionById_ShouldThrowException_ForNonExistentId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.findProjectionById(999L));

        assertTrue(exception.getMessage().contains("Сотрудник не найден"));
    }

    @Test
    @Order(6)
    void compareEntityVsProjection_DataConsistency() {
        for (long id = 1; id <= 10; id++) {
            Employee entity = employeeService.findById(id);
            EmployeeProjection projection = employeeService.findProjectionById(id);

            assertEquals(entity.getPosition(), projection.getPosition());

            assertEquals(entity.getFirstName() + " " + entity.getLastName(),
                    projection.getFullName());
        }
    }

}
