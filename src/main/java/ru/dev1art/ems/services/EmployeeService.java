package ru.dev1art.ems.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.domain.mapper.EmployeeMapper;
import ru.dev1art.ems.domain.model.Employee;
import ru.dev1art.ems.repos.EmployeeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;

    public void saveEmployee(EmployeeDTO employeeDTO) {
        employeeRepository.save(employeeMapper.toEntity(employeeDTO));
    }

    public EmployeeDTO findById(Integer id) {
        return employeeRepository
                .findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Employee with ID " + id + " not found."));
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository
                .findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public int getAgeAtHire(EmployeeDTO employeeDTO) {
            return Period.between(employeeDTO.birthDate(), employeeDTO.hireDate()).getYears();
    }

    public int getCurrentAge(Employee employee) {
        return Period.between(employee.getBirthDate(), LocalDate.now()).getYears();
    }

    public List<EmployeeDTO> getEmployeesInDepartmentYoungerThan(Integer deptNo, Integer age) {
        return employeeRepository
                .findEmployeesInDepartmentYoungerThan(deptNo, age)
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public BigDecimal getMinimumSalary() {
        return employeeRepository.findMinimumSalary();
    }

    public List<EmployeeDTO> getTop5BySalary() {
        return employeeRepository
                .findTop5BySalaryOrderBySalaryDesc()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public void increaseSalaryForLongTermEmployees(BigDecimal percentageIncrease, Integer yearsWorked) {
        List<Employee> eligibleEmployees = employeeRepository.findEmployeesWorkingForGivenAmountOfYearsOrMore(yearsWorked);
        for (Employee employee : eligibleEmployees) {
            BigDecimal newSalary = employee.getSalary()
                    .multiply(BigDecimal.ONE.add(percentageIncrease.divide(new BigDecimal("100"), RoundingMode.DOWN)));
            employee.setSalary(newSalary);
            employeeRepository.save(employee);
        }
    }
    public void deleteOldEmployees(Integer age) {
        employeeRepository.deleteEmployeesOlderThan(age);
    }

    public EmployeeDTO mergeDTOs(
            EmployeeDTO originalEmployee, EmployeeDTO updatedEmployee) {
        return EmployeeDTO
                .builder()
                .id(originalEmployee.id())
                .lastName(updatedEmployee.lastName())
                .position(updatedEmployee.position())
                .birthDate(updatedEmployee.birthDate())
                .hireDate(updatedEmployee.hireDate())
                .departmentNumber(updatedEmployee.departmentNumber())
                .salary(updatedEmployee.salary())
                .build();
    }
    public EmployeeDTO fromTextToDTO(
            String lastName, String position, String birthDate,
            String hireDate, String depNumber, String salary
    ) {
        EmployeeDTO employeeDTO = null;
        try {
            employeeDTO = EmployeeDTO
                    .builder()
                    .lastName(lastName)
                    .position(position)
                    .birthDate(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .hireDate(LocalDate.parse(hireDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .departmentNumber(Integer.parseInt(depNumber))
                    .salary(new BigDecimal(salary))
                    .build();

        } catch (NumberFormatException | DateTimeParseException exception) {
            //TODO
        }

        return employeeDTO;
    }
}
