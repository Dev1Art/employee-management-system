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
@Cacheable
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "lastName", length = 25)
    @NotBlank(message = "Last name is mandatory!")
    private String lastName;
    @NotBlank(message = "Employee position is mandatory!")
    private String position;
    @Column(name = "birthDate")
    @NotNull(message = "Date of birth can not be empty!")
    @Past(message = "Date of birth must be in the past!")
    private LocalDate birthDate;
    @Column(name = "hireDate")
    @NotNull(message = "Hire date can not be empty!")
    @PastOrPresent(message = "Hire date should be present or in the past!")
    private LocalDate hireDate;
    @Column(name = "departmentNumber")
    @Min(value = 0)
    private Integer departmentNumber;
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1000000.0", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal salary;
}
