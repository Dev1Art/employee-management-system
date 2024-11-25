package ru.dev1art.ems.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.domain.model.Employee;

/**
 * @author Dev1Art
 * @project EMS
 * @date 24.11.2024
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {
    Employee toEntity( EmployeeDTO employeeDTO);
    EmployeeDTO toDto(Employee employee);
}
