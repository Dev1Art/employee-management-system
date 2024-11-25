package ru.dev1art.ems.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PopUpControllerTest extends ApplicationTest {
    @InjectMocks PopUpController popUpController;
    @Mock EmployeeService employeeService;
    @Mock MainController mainController;
    Label popUpTitle;
    Button submitButton;
    TextField lastNameField;
    TextField positionField;
    TextField birthDateField;
    TextField hireDateField;
    TextField departmentNumberField;
    TextField salaryField;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/PopUpController.fxml"));
        Parent root = loader.load();
        popUpController = loader.getController();
        popUpController.setMainController(mainController);

        MockitoAnnotations.openMocks(this);

        popUpTitle = new Label();
        submitButton = new Button();
        lastNameField = new TextField();
        positionField = new TextField();
        birthDateField = new TextField();
        hireDateField = new TextField();
        departmentNumberField = new TextField();
        salaryField = new TextField();

        popUpController.setPopUpTitle(popUpTitle);
        popUpController.setSubmitButton(submitButton);
        popUpController.setLastNameField(lastNameField);
        popUpController.setPositionField(positionField);
        popUpController.setBirthDateField(birthDateField);
        popUpController.setHireDateField(hireDateField);
        popUpController.setDepartmentNumberField(departmentNumberField);
        popUpController.setSalaryField(salaryField);

        popUpController.initialize(null, null);
    }

    @Test
    public void testAddEmployee() {
        lastNameField.setText("Doe");
        positionField.setText("Developer");
        birthDateField.setText("1990-01-01");
        hireDateField.setText("2020-01-01");
        departmentNumberField.setText("1");
        salaryField.setText("50000");

        EmployeeDTO mockEmployee = EmployeeDTO.builder().build();
        when(employeeService.fromTextToDTO(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockEmployee);

        popUpController.addEmployee();

        verify(employeeService).saveEmployee(mockEmployee);
        verify(popUpController.getMainController()).refreshTable();
    }

    @Test
    public void testUpdateEmployee() {
        popUpController.setEditingMode(true);
        EmployeeDTO existingEmployee =
                EmployeeDTO
                        .builder()
                        .lastName("Doe")
                        .position("Dev")
                        .build();
        popUpController.setEmployeeToUpdate(existingEmployee);

        lastNameField.setText("Doe");
        positionField.setText("Senior Developer");
        birthDateField.setText("1990-01-01");
        hireDateField.setText("2020-01-01");
        departmentNumberField.setText("1");
        salaryField.setText("60000");

        when(employeeService.mergeDTOs(any(), any())).thenReturn(existingEmployee);

        popUpController.updateEmployee();

        verify(employeeService).saveEmployee(existingEmployee);
        verify(popUpController.getMainController()).refreshTable();
    }

    @Test
    public void testPopulateTextFieldsForEditing() {
        EmployeeDTO employeeDTO =
                EmployeeDTO
                        .builder()
                        .lastName("Doe")
                        .position("Dev")
                        .birthDate(Date.valueOf("1990-01-01").toLocalDate())
                        .hireDate(Date.valueOf("2020-01-01").toLocalDate())
                        .departmentNumber(1)
                        .salary(new BigDecimal(50000))
                        .build();

        popUpController.populateTextFieldsForEditing(employeeDTO);

        assertEquals("Doe", lastNameField.getText());
        assertEquals("Dev", positionField.getText());
        assertEquals("1990-01-01", birthDateField.getText());
        assertEquals("2020-01-01", hireDateField.getText());
        assertEquals("1", departmentNumberField.getText());
        assertEquals("50000", salaryField.getText());
        assertEquals(employeeDTO, popUpController.getEmployeeToUpdate());
    }

    @Test
    public void testChangeLanguage() {
        popUpController.changeLanguage();

        assertNotNull(submitButton.textProperty().get());
        assertNotNull(popUpTitle.textProperty().get());
        assertNotNull(lastNameField.getPromptText());
        assertNotNull(positionField.getPromptText());
        assertNotNull(birthDateField.getPromptText());
        assertNotNull(hireDateField.getPromptText());
        assertNotNull(departmentNumberField.getPromptText());
        assertNotNull(salaryField.getPromptText());
    }

    @Test
    public void testLocaleChanged() {
        PopUpController mockPopUpController = Mockito.mock(PopUpController.class);
        popUpController.setMainController(mainController);
        popUpController.localeChanged(Locale.ENGLISH);
        verify(mockPopUpController).changeLanguage();
    }
}
