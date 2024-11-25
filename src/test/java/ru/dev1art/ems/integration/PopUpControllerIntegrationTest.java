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
import ru.dev1art.ems.controllers.PopUpController;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class PopUpControllerIntegrationTest {
    @MockBean EmployeeService employeeService;
    @InjectMocks @Autowired PopUpController popUpController;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/PopUpController.fxml"));
        Parent root = loader.load();
        popUpController.initialize(null, null);
    }

    @Test
    public void testAddEmployee() {
        EmployeeDTO newEmployee = new EmployeeDTO(null, "Alice", "Designer", LocalDate.now(),
                LocalDate.now(), 3, BigDecimal.valueOf(70000));
        when(employeeService.fromTextToDTO(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(newEmployee);

        popUpController.getLastNameField().setText("Alice");
        popUpController.getPositionField().setText("Designer");
        popUpController.getHireDateField().setText(LocalDate.now().toString());
        popUpController.getBirthDateField().setText(LocalDate.now().toString());
        popUpController.getDepartmentNumberField().setText("3");
        popUpController.getSalaryField().setText("70000");
        popUpController.getSubmitButton().fire();

        verify(employeeService).saveEmployee(newEmployee);
    }

    @Test
    public void testUpdateEmployee() {
        EmployeeDTO existingEmployee = new EmployeeDTO(1, "John Doe",
                "Developer", LocalDate.now(), LocalDate.now(), 1, BigDecimal.valueOf(50000));
        when(employeeService.fromTextToDTO(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(existingEmployee);
        popUpController.setEmployeeToUpdate(existingEmployee);

        popUpController.getLastNameField().setText("John Doe");
        popUpController.getPositionField().setText("Senior Developer");
        popUpController.getSubmitButton().fire();

        verify(employeeService).saveEmployee(existingEmployee);
    }
}
