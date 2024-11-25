package ru.dev1art.ems.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.domain.mapper.EmployeeMapper;
import ru.dev1art.ems.domain.model.Employee;
import ru.dev1art.ems.repos.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeServiceTest {
    @InjectMocks EmployeeService employeeService;
    @Mock EmployeeRepository employeeRepository;
    @Mock EmployeeMapper employeeMapper;
    EmployeeDTO employeeDTO;
    Employee employee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeDTO = EmployeeDTO.builder()
                .id(1)
                .lastName("Doe")
                .position("Developer")
                .birthDate(LocalDate.of(1990, 1, 1))
                .hireDate(LocalDate.of(2020, 1, 1))
                .departmentNumber(1)
                .salary(new BigDecimal("50000"))
                .build();

        employee = new Employee();
        employee.setId(1);
        employee.setLastName("Doe");
        employee.setPosition("Developer");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setHireDate(LocalDate.of(2020, 1, 1));
        employee.setDepartmentNumber(1);
        employee.setSalary(new BigDecimal("50000"));
    }

    @Test
    public void testSaveEmployee() {
        when(employeeMapper.toEntity(employeeDTO)).thenReturn(employee);

        employeeService.saveEmployee(employeeDTO);

        verify(employeeRepository).save(employee);
    }

    @Test
    public void testFindById_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO foundEmployee = employeeService.findById(1);

        assertEquals(employeeDTO, foundEmployee);
        verify(employeeRepository).findById(1);
    }

    @Test
    public void testFindById_NotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeService.findById(1);
        });

        assertEquals("Employee with ID 1 not found.", exception.getMessage());
        verify(employeeRepository).findById(1);
    }

    @Test
    public void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(1);

        employeeService.deleteEmployee(1);

        verify(employeeRepository).deleteById(1);
    }

    @Test
    public void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        assertEquals(1, employees.size());
        assertEquals(employeeDTO, employees.get(0));
        verify(employeeRepository).findAll();
    }

    @Test
    public void testGetAgeAtHire() {
        Integer age = employeeService.getAgeAtHire(employeeDTO);
        assertEquals(30, age);
    }

    @Test
    public void testGetCurrentAge() {
        Integer age = employeeService.getCurrentAge(employeeDTO);
        assertEquals(34, age);
    }

    @Test
    public void testGetEmployeesInDepartmentYoungerThan() {
        when(employeeRepository.findEmployeesInDepartmentYoungerThan(1, 30)).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> employees = employeeService.getEmployeesInDepartmentYoungerThan(1, 30);

        assertEquals(1, employees.size());
        assertEquals(employeeDTO, employees.get(0));
        verify(employeeRepository).findEmployeesInDepartmentYoungerThan(1, 30);
    }

    @Test
    public void testIncreaseSalaryForLongTermEmployees() {
        when(employeeRepository.findEmployeesWorkingForGivenAmountOfYearsOrMore(5)).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> updatedEmployees = employeeService.increaseSalaryForLongTermEmployees(new BigDecimal("10"), 5);

        assertEquals(1, updatedEmployees.size());
        assertEquals(employeeDTO, updatedEmployees.get(0));
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testFromTextToDTO_ValidInput() {
        EmployeeDTO dto = employeeService.fromTextToDTO("Doe", "Developer", "1990-01-01", "2020-01-01", "1", "50000");

        assertNotNull(dto);
        assertEquals("Doe", dto.lastName());
        assertEquals("Developer", dto.position());
        assertEquals(LocalDate.of(1990, 1, 1), dto.birthDate());
        assertEquals(LocalDate.of(2020, 1, 1), dto.hireDate());
        assertEquals(1, dto.departmentNumber());
        assertEquals(new BigDecimal("50000"), dto.salary());
    }

    @Test
    public void testFromTextToDTO_InvalidInput() {
        EmployeeDTO dto = employeeService.fromTextToDTO("Doe", "Developer", "invalid-date", "2020-01-01", "1", "50000");

        assertNull(dto);
    }
}
