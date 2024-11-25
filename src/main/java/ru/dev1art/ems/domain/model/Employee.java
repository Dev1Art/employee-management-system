package ru.dev1art.ems.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "position")
    private String position;
    @Column(name = "birthDate")
    private LocalDate birthDate;
    @Column(name = "hireDate")
    private LocalDate hireDate;
    @Column(name = "departmentNumber")
    private Integer departmentNumber;
    @Column(name = "salary")
    private BigDecimal salary;
}
