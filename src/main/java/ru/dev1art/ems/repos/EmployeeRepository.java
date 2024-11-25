package ru.dev1art.ems.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dev1art.ems.domain.model.Employee;
import java.util.List;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * incorporate native SQL within JPQL query using FUNCTION()
     */
    @Query("SELECT e FROM Employee e WHERE e.departmentNumber = :deptNo " +
            "AND FUNCTION('DATEDIFF', YEAR, e.birthDate, CURRENT_DATE) < :age")
    List<Employee> findEmployeesInDepartmentYoungerThan(@Param("deptNo") Integer deptNo, @Param("age") Integer age);

    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC LIMIT 5")
    List<Employee> findTop5BySalaryDesc();

    @Query("SELECT e FROM Employee e ORDER BY e.salary ASC LIMIT 5")
    List<Employee> findTop5BySalaryAsc();

    @Query("SELECT e FROM Employee e WHERE FUNCTION('DATEDIFF', YEAR, e.hireDate, CURRENT_DATE) >= :years")
    List<Employee> findEmployeesWorkingForGivenAmountOfYearsOrMore(@Param("years") Integer years);

    @Query("SELECT e FROM Employee e WHERE FUNCTION('DATEDIFF', YEAR, e.birthDate, CURRENT_DATE) > :age")
    List<Employee> findEmployeesOlderThan(@Param("age") Integer age);
}