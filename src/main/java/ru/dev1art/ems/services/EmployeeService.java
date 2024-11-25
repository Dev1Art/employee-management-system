package ru.dev1art.ems.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

@Slf4j
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;
    static final Marker SERVICE_MARKER = MarkerFactory.getMarker("SERVICE");
    static final Marker DATA_MARKER = MarkerFactory.getMarker("DATA");

    /**
     * Saves a new employee to the database.
     *
     * @param employeeDTO The EmployeeDTO object containing the employee's information to be saved.
     */
    public void saveEmployee(EmployeeDTO employeeDTO) {
        log.debug(SERVICE_MARKER, "Saving employee: {}", employeeDTO);
        employeeRepository.save(employeeMapper.toEntity(employeeDTO));
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id The ID of the employee to find.
     * @return The EmployeeDTO object corresponding to the found employee.
     * @throws EntityNotFoundException if the employee with the given ID does not exist.
     */
    public EmployeeDTO findById(Integer id) {
        log.debug(SERVICE_MARKER, "Finding employee by ID: {}", id);
        return employeeRepository
                .findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> {
                    log.warn(DATA_MARKER, "Employee not found for ID: {}", id);
                    return new EntityNotFoundException("Employee with ID " + id + " not found.");
                });
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id The ID of the employee to delete.
     */
    public void deleteEmployee(Integer id) {
        log.debug(SERVICE_MARKER, "Deleting employee with ID: {}", id);
        try {
            employeeRepository.deleteById(id);
            log.info(DATA_MARKER, "Employee with ID {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Error deleting employee with ID {}: ", id, e);
        }

    }

    /**
     * Retrieves all employees from the database.
     *
     * @return A list of EmployeeDTO objects representing all employees.
     */
    public List<EmployeeDTO> getAllEmployees() {
        log.debug(SERVICE_MARKER, "Retrieving all employees");
        List<EmployeeDTO> employees = employeeRepository
                .findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Retrieved {} employees", employees.size());
        return employees;

    }

    /**
     * Calculates the age of an employee at the time of hiring.
     *
     * @param employeeDTO The EmployeeDTO object containing the employee's birth date and hire date.
     * @return The age of the employee at the time of hiring in years.
     */
    public Integer getAgeAtHire(EmployeeDTO employeeDTO) {
        log.debug(SERVICE_MARKER, "Calculating current age for employee: {}", employeeDTO);
        Integer years = Period.between(employeeDTO.birthDate(), employeeDTO.hireDate()).getYears();
        log.debug(DATA_MARKER, "Calculated age: {}", years);
        return years;
    }

    /**
     * Calculates the current age of an employee.
     *
     * @param employeeDTO The EmployeeDTO object containing the employee's birth date.
     * @return The current age of the employee in years.
     */
    public Integer getCurrentAge(EmployeeDTO employeeDTO) {
        log.debug(SERVICE_MARKER, "Calculating current age for employee: {}", employeeDTO);
        Integer age = Period.between(employeeDTO.birthDate(), LocalDate.now()).getYears();
        log.debug(DATA_MARKER, "Calculated age: {}", age);
        return age;
    }

    /**
     * Retrieves a list of employees in a specific department who are younger than a given age.
     *
     * @param deptNo The department number to filter employees by.
     * @param age The maximum age of employees to retrieve.
     * @return A list of EmployeeDTO objects representing the filtered employees.
     */
    public List<EmployeeDTO> getEmployeesInDepartmentYoungerThan(Integer deptNo, Integer age) {
        log.debug(SERVICE_MARKER, "Finding employees in department {} younger than {}", deptNo, age);
        List<EmployeeDTO> employees = employeeRepository
                .findEmployeesInDepartmentYoungerThan(deptNo, age)
                .stream()
                .map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Found {} employees", employees.size());
        return employees;
    }

    /**
     * Retrieves the top 5 employees with the minimum salary.
     *
     * @return A list of EmployeeDTO objects representing the employees with the lowest salaries.
     */
    public List<EmployeeDTO> getEmployeesWithMinSalary() {
        log.debug(SERVICE_MARKER, "Finding employees with minimum salary");
        List<EmployeeDTO> employees = employeeRepository
                .findTop5BySalaryAsc()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Found {} employees with minimum salary", employees.size());
        return employees;
    }

    /**
     * Retrieves the top 5 employees with the maximum salary.
     *
     * @return A list of EmployeeDTO objects representing the employees with the highest salaries.
     */
    public List<EmployeeDTO> getEmployeesWithMaxSalary() {
        log.debug(SERVICE_MARKER, "Finding employees with maximum salary");
        List<EmployeeDTO> employees = employeeRepository
                .findTop5BySalaryDesc()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Found {} employees with maximum salary", employees.size());
        return employees;
    }

    /**
     * Retrieves a list of employees who have worked for a specified number of years or more.
     *
     * @param yearsWorked The number of years to filter employees by.
     * @return A list of EmployeeDTO objects representing long-term employees.
     */
    public List<EmployeeDTO> getLongTermEmployees(Integer yearsWorked) {
        log.debug(SERVICE_MARKER, "Finding long-term employees ({} years or more)", yearsWorked);
        List<EmployeeDTO> employees = employeeRepository
                .findEmployeesWorkingForGivenAmountOfYearsOrMore(yearsWorked)
                .stream()
                .map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Found {} long-term employees", employees.size());
        return employees;
    }

    /**
     * Increases the salary of long-term employees by a specified percentage.
     *
     * @param percentageIncrease The percentage by which to increase salaries.
     * @param yearsWorked The minimum number of years an employee must have worked to be eligible for the increase.
     * @return A list of EmployeeDTO objects representing employees whose salaries were increased.
     * @throws IllegalArgumentException if percentageIncrease or yearsWorked is null.
     */
    public List<EmployeeDTO> increaseSalaryForLongTermEmployees(BigDecimal percentageIncrease, Integer yearsWorked) {
        log.debug(SERVICE_MARKER, "Increasing salary for long-term employees ({} years, {}% increase)",
                yearsWorked, percentageIncrease);
        if (percentageIncrease == null || yearsWorked == null) {
            log.error("Percentage increase and years worked cannot be null");
            throw new IllegalArgumentException("Percentage increase and years worked must not be null");
        }
        List<Employee> eligibleEmployees = employeeRepository.findEmployeesWorkingForGivenAmountOfYearsOrMore(yearsWorked);
        log.info(DATA_MARKER, "Found {} eligible employees", eligibleEmployees.size());
        for (Employee employee : eligibleEmployees) {
            BigDecimal newSalary = employee.getSalary().multiply(BigDecimal.ONE.
                    add(percentageIncrease.divide(new BigDecimal("100"), RoundingMode.DOWN)));
            employee.setSalary(newSalary);
            employeeRepository.save(employee);
            log.debug(DATA_MARKER, "Updated salary for employee {} to {}", employee.getId(), newSalary);
        }

        return eligibleEmployees
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a list of employees older than a specified age.
     *
     * @param age The minimum age to filter employees by.
     * @return A list of EmployeeDTO objects representing employees older than the specified age.
     */
    public List<EmployeeDTO> findOldEmployees(Integer age) {
        log.debug(SERVICE_MARKER, "Finding employees older than {}", age);
        List<EmployeeDTO> employees = employeeRepository
                .findEmployeesOlderThan(age)
                .stream().map(employeeMapper::toDto)
                .toList();
        log.info(DATA_MARKER, "Found {} employees older than {}", employees.size(), age);
        return employees;
    }

    /**
     * Merges two EmployeeDTO objects, updating the original employee's data with the updated employee's data.
     *
     * @param originalEmployee The original EmployeeDTO object.
     * @param updatedEmployee The updated EmployeeDTO object.
     * @return A new EmployeeDTO object that combines the original and updated data.
     */
    public EmployeeDTO mergeDTOs(
            EmployeeDTO originalEmployee, EmployeeDTO updatedEmployee) {
        log.debug(SERVICE_MARKER, "Merging employee DTOs: original={}, updated={}", originalEmployee, updatedEmployee);
        EmployeeDTO mergedDTO = EmployeeDTO.builder()
                .id(originalEmployee.id())
                .lastName(updatedEmployee.lastName())
                .position(updatedEmployee.position())
                .birthDate(updatedEmployee.birthDate())
                .hireDate(updatedEmployee.hireDate())
                .departmentNumber(updatedEmployee.departmentNumber())
                .salary(updatedEmployee.salary())
                .build();
        log.debug(DATA_MARKER, "Merged DTO: {}", mergedDTO);
        return mergedDTO;
    }

    /**
     * Converts text input into an EmployeeDTO object.
     *
     * @param lastName The last name of the employee.
     * @param position The position of the employee.
     * @param birthDate The birth date of the employee in "yyyy-MM-dd" format.
     * @param hireDate The hire date of the employee in "yyyy-MM-dd" format.
     * @param depNumber The department number of the employee.
     * @param salary The salary of the employee.
     * @return An EmployeeDTO object created from the provided text input, or null if the input is invalid.
     */
    public EmployeeDTO fromTextToDTO(
            String lastName, String position, String birthDate,
            String hireDate, String depNumber, String salary
    ) {
        log.debug(SERVICE_MARKER, "Converting text input to EmployeeDTO");
        try {
            EmployeeDTO employeeDTO = EmployeeDTO
                    .builder()
                    .lastName(lastName)
                    .position(position)
                    .birthDate(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .hireDate(LocalDate.parse(hireDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .departmentNumber(Integer.parseInt(depNumber))
                    .salary(new BigDecimal(salary))
                    .build();
            log.debug(DATA_MARKER, "Created EmployeeDTO: {}", employeeDTO);
            return employeeDTO;
        } catch (NumberFormatException | DateTimeParseException exception) {
            log.error("Invalid input data for EmployeeDTO: {}", exception.getMessage(), exception);
            return null;
        }
    }
}
