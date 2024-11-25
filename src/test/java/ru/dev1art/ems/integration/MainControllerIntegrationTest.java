package ru.dev1art.ems.integration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.framework.junit5.ApplicationExtension;
import ru.dev1art.ems.config.EMS;
import ru.dev1art.ems.controllers.MainController;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@ExtendWith(ApplicationExtension.class)
@SpringBootTest
@ContextConfiguration(classes = EMS.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainControllerIntegrationTest {
    @MockBean EmployeeService employeeService;
    @InjectMocks @Autowired MainController mainController;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/MainController.fxml"));
        Parent root = loader.load();
        mainController.initialize(null, null);
    }

    @Test
    public void testLoadEmployeeData() {
        EmployeeDTO employee1 = new EmployeeDTO(1, "Doe", "Dev", LocalDate.now(), LocalDate.now(), 1, BigDecimal.valueOf(50000));
        EmployeeDTO employee2 = new EmployeeDTO(2, "Doe", "Manager", LocalDate.now(), LocalDate.now(), 2, BigDecimal.valueOf(60000));
        List<EmployeeDTO> employees = Arrays.asList(employee1, employee2);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mainController.loadEmployeeData();

        assertEquals(2, mainController.getEmployeeTable().getItems().size());
        assertEquals("John Doe", mainController.getEmployeeTable().getItems().get(0).lastName());
    }

    @Test
    public void testAddEmployee() {
        EmployeeDTO newEmployee = new EmployeeDTO(null, "Alice", "Designer", LocalDate.now(), LocalDate.now(), 3, BigDecimal.valueOf(70000));
        when(employeeService.fromTextToDTO(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(newEmployee);

        mainController.getAddEmployeeButton().fire();

        verify(employeeService).saveEmployee(newEmployee);
    }

    @Test
    public void testDeleteEmployee() {
        EmployeeDTO employee = new EmployeeDTO(1, "John Doe", "Developer", LocalDate.now(), LocalDate.now(), 1, BigDecimal.valueOf(50000));
        mainController.getEmployeeTable().getItems().add(employee);

        mainController.getDeleteEmployeeButton().fire();

        verify(employeeService).deleteEmployee(employee.id());
        assertEquals(0, mainController.getEmployeeTable().getItems().size());
    }
}

