package ru.dev1art.ems.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dev1art.ems.entities.Employee;
import ru.dev1art.ems.repos.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public Employee findById(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.get();
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public int getAgeAtHire(Employee employee) {
        return Period.between(employee.getBirthDate(), employee.getHireDate()).getYears();
    }

    public int getCurrentAge(Employee employee) {
        return Period.between(employee.getBirthDate(), LocalDate.now()).getYears();
    }

    public List<Employee> getEmployeesInDepartmentYoungerThan(Integer deptNo, Integer age) {
        return employeeRepository.findEmployeesInDepartmentYoungerThan(deptNo, age);
    }

    public BigDecimal getMinimumSalary() {
        return employeeRepository.findMinimumSalary();
    }

    public List<Employee> getTop5BySalary() {
        return employeeRepository.findTop5BySalaryOrderBySalaryDesc();
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

}
