package ru.dev1art.ems.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuControllerTest extends ApplicationTest {
    @InjectMocks MenuController menuController;
    @Mock MainController mainController;
    @Mock EmployeeService employeeService;
    Label menuLabel;
    ComboBox<String> shortcutsComboBox;
    TextField valueField;
    Button findButton;

    @BeforeEach
    public void setUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/MenuController.fxml"));
        Parent root = loader.load();
        menuController = loader.getController();
        menuController.setMainController(mainController);
        MockitoAnnotations.openMocks(this);

        menuLabel = new Label();
        shortcutsComboBox = new ComboBox<>();
        valueField = new TextField();
        findButton = new Button();

        menuController.setMenuLabel(menuLabel);
        menuController.setShortcutsComboBox(shortcutsComboBox);
        menuController.setValueField(valueField);
        menuController.setFindButton(findButton);
        menuController.initialize(null, null);
    }

    @Test
    public void testFindYounger() {
        String input = "1:25";
        valueField.setText(input);
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.getEmployeesInDepartmentYoungerThan(1, 25)).thenReturn(mockEmployees);

        menuController.findYounger();

        verify(employeeService).getEmployeesInDepartmentYoungerThan(1, 25);

    }

    @Test
    public void testFindMinSalary() {
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.getEmployeesWithMinSalary()).thenReturn(mockEmployees);

        menuController.findMinSalary();

        verify(employeeService).getEmployeesWithMinSalary();
    }

    @Test
    public void testFindHighestSalary() {
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.getEmployeesWithMaxSalary()).thenReturn(mockEmployees);

        menuController.findHighestSalary();

        verify(employeeService).getEmployeesWithMaxSalary();
    }

    @Test
    public void testFindWorkingSince() {
        valueField.setText("5");
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.getLongTermEmployees(5)).thenReturn(mockEmployees);

        menuController.findWorkingSince();

        verify(employeeService).getLongTermEmployees(5);
    }

    @Test
    public void testFindOlderThan() {
        valueField.setText("30");
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.findOldEmployees(30)).thenReturn(mockEmployees);

        menuController.findOlderThan();

        verify(employeeService).findOldEmployees(30);
    }

    @Test
    public void testGetAgeOfEmployee() {
        valueField.setText("1");
        EmployeeDTO mockEmployee = EmployeeDTO.builder().build();
        when(employeeService.findById(1)).thenReturn(mockEmployee);
        when(employeeService.getCurrentAge(mockEmployee)).thenReturn(30);

        menuController.getAgeOfEmployee();

        verify(employeeService).findById(1);
        verify(employeeService).getCurrentAge(mockEmployee);
        assertEquals("Current age of employee: 30", valueField.getText());
    }

    @Test
    public void testGetAgeOfEmployeeWhenHired() {
        // Setup
        valueField.setText("1");
        EmployeeDTO mockEmployee = EmployeeDTO.builder().build();
        when(employeeService.findById(1)).thenReturn(mockEmployee);
        when(employeeService.getAgeAtHire(mockEmployee)).thenReturn(25);

        menuController.getAgeOfEmployeeWhenHired();

        verify(employeeService).findById(1);
        verify(employeeService).getAgeAtHire(mockEmployee);
        assertEquals("Age at hire: 25", valueField.getText());
    }

    @Test
    public void testIncreaseSalaryForLongTermEmployees() {
        // Setup
        valueField.setText("5:2");
        List<EmployeeDTO> mockEmployees = new ArrayList<>();
        mockEmployees.add(EmployeeDTO.builder().build());
        when(employeeService.increaseSalaryForLongTermEmployees(new BigDecimal("5"), 2)).thenReturn(mockEmployees);

        menuController.increaseSalaryForLongTermEmployees();

        verify(employeeService).increaseSalaryForLongTermEmployees(new BigDecimal("5"), 2);
    }
}
