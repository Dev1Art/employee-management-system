package ru.dev1art.ems.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dev1art.ems.entities.Employee;
import ru.dev1art.ems.services.EmployeeService;
import ru.dev1art.ems.util.I18NUtil;

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
public class MainController implements Initializable {

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
    private ToggleButton menuButton;
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
    private boolean isEnglishLocale = false;

    public MainController() {}

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
            // addEmployee
        });

        updateEmployeeButton.setOnMouseClicked(action -> {
            // new fxml form
        });

        deleteEmployeeButton.setOnMouseClicked(action -> {
            Employee employee = employeeTable.getSelectionModel().getSelectedItems().get(0);
            employeeService.deleteEmployee(employee.getId());
            refreshTable();
        });

        refreshTableButton.setOnMouseClicked(action -> refreshTable());

        languageChangerButton.setOnMouseClicked(action -> {
            if(!isEnglishLocale) {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(0));
                isEnglishLocale = true;
            } else {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(1));
                isEnglishLocale = false;
            }
            addEmployeeButton.textProperty().bind(I18NUtil.createStringBinding("addEmployeeButton"));
            deleteEmployeeButton.textProperty().bind(I18NUtil.createStringBinding("deleteEmployeeButton"));
            updateEmployeeButton.textProperty().bind(I18NUtil.createStringBinding("updateEmployeeButton"));
            refreshTableButton.textProperty().bind(I18NUtil.createStringBinding("refreshTableButton"));
            languageChangerButton.textProperty().bind(I18NUtil.createStringBinding("languageChangerButton"));
            exitButton.textProperty().bind(I18NUtil.createStringBinding("exitButton"));
            menuButton.textProperty().bind(I18NUtil.createStringBinding("menuButton"));
        });

        exitButton.setOnMouseClicked(action -> {
            System.exit(0);
        });

        menuButton.setOnMouseClicked(action -> {});

    }

}
