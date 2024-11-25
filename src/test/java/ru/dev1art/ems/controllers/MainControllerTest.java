package ru.dev1art.ems.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainControllerTest extends ApplicationTest {
    @Mock EmployeeService employeeService;
    @Mock TableView<EmployeeDTO> employeeTable;
    @Mock TableColumn<EmployeeDTO, Integer> idColumn;
    @Mock TableColumn<EmployeeDTO, String> lastNameColumn;
    @Mock TableColumn<EmployeeDTO, String> positionColumn;
    @Mock Button addEmployeeButton;
    @InjectMocks MainController mainController;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/dev1art/ems/MainController.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        mainController.setEmployeeService(employeeService);
        mainController.initialize(null, null);
        when(employeeTable.getColumns()).thenReturn(FXCollections.observableArrayList(idColumn, lastNameColumn, positionColumn));
    }

    @Test
    public void testInitialize() {
        ObservableList<EmployeeDTO> mockEmployeeList = FXCollections.observableArrayList(
                EmployeeDTO.builder()
                        .id(1)
                        .lastName("James")
                        .position("Dev")
                        .birthDate(LocalDate.now().minusYears(1))
                        .hireDate(LocalDate.now())
                        .departmentNumber(12)
                        .salary(new BigDecimal(50000))
                        .build()
        );
        when(employeeService.getAllEmployees()).thenReturn(mockEmployeeList);

        mainController.initialize(null, null);

        verify(employeeService, times(1)).getAllEmployees();
        verify(employeeTable, times(1)).setItems(mockEmployeeList);
    }

    @Test
    public void testLoadEmployeeData() {
        List<EmployeeDTO> employees = List.of(
                EmployeeDTO
                        .builder()
                        .id(1)
                        .lastName("Smith")
                        .position("Eng")
                        .birthDate(LocalDate.of(1985, 1, 1))
                        .hireDate( LocalDate.of(2010, 1, 1))
                        .departmentNumber(10)
                        .salary(new BigDecimal(60000))
                        .build()
        );
        when(employeeService.getAllEmployees()).thenReturn(employees);
        mainController.loadEmployeeData();
        ArgumentCaptor<ObservableList<EmployeeDTO>> captor = ArgumentCaptor.forClass(ObservableList.class);
        verify(employeeTable).setItems(captor.capture());
    }

    @Test
    public void testAddEmployeeButtonAction() {
        mainController.addButtonsActionOnClick();
        verify(addEmployeeButton, times(1)).setOnMouseClicked(any());
    }
}
