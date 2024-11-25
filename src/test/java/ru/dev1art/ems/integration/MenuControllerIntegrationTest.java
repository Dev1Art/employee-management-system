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
import ru.dev1art.ems.controllers.MenuController;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class MenuControllerIntegrationTest {
    @MockBean EmployeeService employeeService;
    @InjectMocks @Autowired MenuController menuController;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/MenuController.fxml"));
        Parent root = loader.load();
        menuController.initialize(null, null);
    }

    @Test
    public void testFindYounger() {
        // Given
        EmployeeDTO employee1 = new EmployeeDTO(1, "John Doe",
                "Developer", LocalDate.now().minusYears(20),
                LocalDate.now(), 1, BigDecimal.valueOf(50000));
        EmployeeDTO employee2 = new EmployeeDTO(2, "Jane Doe",
                "Manager", LocalDate.now().minusYears(30),
                LocalDate.now(), 2, BigDecimal.valueOf(60000));
        when(employeeService.getEmployeesInDepartmentYoungerThan(1, 25)).thenReturn(List.of(employee1));

        menuController.getValueField().setText("1:25");
        menuController.getFindButton().fire();

        verify(employeeService).getEmployeesInDepartmentYoungerThan(1, 25);
        assertEquals(1, menuController.getMainController().getEmployeeTable().getItems().size());
        assertEquals("John Doe", menuController.getMainController().getEmployeeTable().getItems().get(0).lastName());
    }
}
