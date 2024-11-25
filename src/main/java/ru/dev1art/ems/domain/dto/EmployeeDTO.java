package ru.dev1art.ems.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Dev1Art
 * @project EMS
 * @date 24.11.2024
 */

@Builder
public record EmployeeDTO(
        @NotNull(message = "ID is mandatory!")
        Integer id,
        @NotBlank(message = "Last name is mandatory!")
        String lastName,
        @NotBlank(message = "Employee position is mandatory!")
        String position,
        @NotNull(message = "Date of birth cannot be empty!")
        @Past(message = "Date of birth must be in the past!")
        LocalDate birthDate,
        @NotNull(message = "Hire date cannot be empty!")
        @PastOrPresent(message = "Hire date must be between present and past!")
        LocalDate hireDate,
        @Positive(message = "Department number cannot be negative!")
        @NotNull(message = "Department number cannot be null!")
        Integer departmentNumber,
        @DecimalMin(value = "0.0", inclusive = false)
        @DecimalMax(value = "1000000.0", inclusive = false)
        @Digits(integer = 7, fraction = 2)
        BigDecimal salary
) {
}
