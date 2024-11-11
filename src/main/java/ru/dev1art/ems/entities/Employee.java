package ru.dev1art.ems.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "lastName", length = 25)
    @NotBlank(message = "Last name is mandatory!")
    private String last_name;
    @NotBlank(message = "Employee position is mandatory!")
    private String position;
    @Column(name = "birthDate")
    @NotNull(message = "Date of birth can not be empty!")
    @Past(message = "Date of birth must be in the past!")
    private LocalDate birth_date;
    @Column(name = "hireDate")
    @NotNull(message = "Hire date can not be empty!")
    @PastOrPresent(message = "Hire date should be present or in the past!")
    private LocalDate hire_date;
    @Column(name = "departmentNumber")
    @Min(value = 0)
    private Integer department_number;
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1000000.0", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal salary;
}
