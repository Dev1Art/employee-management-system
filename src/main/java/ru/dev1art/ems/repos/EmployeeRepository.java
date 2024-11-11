package ru.dev1art.ems.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dev1art.ems.entities.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // incorporate native SQL within JPQL query using FUNCTION()
    @Query("SELECT e FROM Employee e WHERE e.department_number = :deptNo " +
            "AND FUNCTION('DATEDIFF', YEAR, e.birth_date, CURRENT_DATE) < :age")
    List<Employee> findEmployeesInDepartmentYoungerThan(Integer deptNo, Integer age);

    @Query("SELECT MIN(e.salary) FROM Employee e")
    BigDecimal findMinimumSalary();

    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC LIMIT 5")
    List<Employee> findTop5BySalaryOrderBySalaryDesc();

    @Query("SELECT e FROM Employee e WHERE FUNCTION('DATEDIFF', YEAR, e.hire_date, CURRENT_DATE) >= :years")
    List<Employee> findEmployeesWorkingForGivenAmountOfYearsOrMore(Integer years);

    @Query("DELETE FROM Employee e WHERE FUNCTION('DATEDIFF', YEAR, e.birth_date, CURRENT_DATE) > :age")
    @Modifying
    void deleteEmployeesOlderThan(Integer age);
}
