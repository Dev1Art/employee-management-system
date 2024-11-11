package ru.dev1art.ems.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dev1art.ems.entities.Employee;
import ru.dev1art.ems.services.EmployeeService;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Component
public class EmployeeController implements Initializable {

    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> position;
    @FXML
    private TableColumn<Employee, LocalDate> birthDate;
    @FXML
    private TableColumn<Employee, LocalDate> hireDate;
    @FXML
    private TableColumn<Employee, Integer> departmentNumber;
    @FXML
    private TableColumn<Employee, BigDecimal> salary;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private Button updateEmployeeButton;
    @FXML
    private Button deleteEmployeeButton;
    @FXML
    private Button refreshTableButton;
    @FXML
    private Button languageChangerButton;
    @FXML
    private Button exitButton;

    @Autowired
    private EmployeeService employeeService;

    public EmployeeController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        birthDate.setCellValueFactory(new PropertyValueFactory<>("birth_date"));
        hireDate.setCellValueFactory(new PropertyValueFactory<>("hire_date"));
        departmentNumber.setCellValueFactory(new PropertyValueFactory<>("department_number"));
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        loadEmployeeData();

        addButtonsActionOnClick();
    }

    private void loadEmployeeData() {
        ObservableList<Employee> employees = FXCollections.observableArrayList(employeeService.getAllEmployees());
        employeeTable.setItems(employees);
    }
    public void refreshTable() {
        employeeTable.getItems().clear();
        loadEmployeeData();
    }

    private void addEmployee(Employee employee) {
        employeeService.saveEmployee(employee);
        refreshTable();
    }

    private void updateEmployee(Employee employee) {
        Employee selectedEmployee = employeeService.findById(employee.getId());
        selectedEmployee.setLast_name(employee.getLast_name());
        selectedEmployee.setPosition(employee.getPosition());
        selectedEmployee.setBirth_date(employee.getBirth_date());
        selectedEmployee.setHire_date(employee.getHire_date());
        selectedEmployee.setDepartment_number(employee.getDepartment_number());
        selectedEmployee.setSalary(employee.getSalary());
        employeeService.saveEmployee(selectedEmployee);
        refreshTable();
    }

    private void deleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        employeeService.deleteEmployee(selectedEmployee.getId());
        refreshTable();
    }

    private void addButtonsActionOnClick() {
        addEmployeeButton.setOnMouseClicked(action -> {
            // new fxml form
        });

        updateEmployeeButton.setOnMouseClicked(action -> {
            // new fxml form
        });

        deleteEmployeeButton.setOnMouseClicked(action -> {
            // new fxml form
        });

        refreshTableButton.setOnMouseClicked(action -> {

        });

        languageChangerButton.setOnMouseClicked(action -> {

        });

        exitButton.setOnMouseClicked(action -> {
            System.exit(0);
        });
    }

}
