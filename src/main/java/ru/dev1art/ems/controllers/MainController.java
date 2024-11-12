package ru.dev1art.ems.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.dev1art.ems.config.SpringFXMLLoader;
import ru.dev1art.ems.entities.Employee;
import ru.dev1art.ems.services.EmployeeService;
import ru.dev1art.ems.util.I18NUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    private TableColumn<Employee, String> positionColumn;
    @FXML
    private TableColumn<Employee, LocalDate> birthDateColumn;
    @FXML
    private TableColumn<Employee, LocalDate> hireDateColumn;
    @FXML
    private TableColumn<Employee, Integer> departmentNumberColumn;
    @FXML
    private TableColumn<Employee, BigDecimal> salaryColumn;
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
    @FXML
    private Button submitButton;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField birthDateField;
    @FXML
    private TextField hireDateField;
    @FXML
    private TextField departmentNumberField;
    @FXML
    private TextField salaryField;
    @Setter
    private Stage mainStage;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ApplicationContext context;
    private boolean isEnglishLocale = false;
    private boolean isUpdateOperation = false;

    public MainController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birth_date"));
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hire_date"));
        departmentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("department_number"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

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

    private void addEmployee() {
        try {
            Employee employee = new Employee();
            String emp_last_name = lastNameField.getText();
            String emp_position = positionField.getText();
            LocalDate emp_birth_date = LocalDate.parse(birthDateField.getText());
            LocalDate emp_hire_date = LocalDate.parse(hireDateField.getText());
            Integer emp_department_number = Integer.parseInt(departmentNumberField.getText());
            BigDecimal emp_salary = new BigDecimal(salaryField.getText());
            employee.setLast_name(emp_last_name);
            employee.setPosition(emp_position);
            employee.setBirth_date(emp_birth_date);
            employee.setHire_date(emp_hire_date);
            employee.setDepartment_number(emp_department_number);
            employee.setSalary(emp_salary);
            employeeService.saveEmployee(employee);

        } catch (NumberFormatException | DateTimeParseException | NullPointerException exception) {
            //TODO
        }
    }

    private void updateEmployee() {
        try {
            Employee employeeToSave = employeeTable.getSelectionModel().getSelectedItems().get(0);
            String emp_last_name = lastNameField.getText();
            String emp_position = positionField.getText();
            LocalDate emp_birth_date = LocalDate.parse(birthDateField.getText());
            LocalDate emp_hire_date = LocalDate.parse(hireDateField.getText());
            Integer emp_department_number = Integer.parseInt(departmentNumberField.getText());
            BigDecimal emp_salary = new BigDecimal(salaryField.getText());
            employeeToSave.setLast_name(emp_last_name);
            employeeToSave.setPosition(emp_position);
            employeeToSave.setBirth_date(emp_birth_date);
            employeeToSave.setHire_date(emp_hire_date);
            employeeToSave.setDepartment_number(emp_department_number);
            employeeToSave.setSalary(emp_salary);
            employeeService.saveEmployee(employeeToSave);
        } catch (NumberFormatException | DateTimeParseException | NullPointerException exception) {
            //TODO
        }
    }

    private void addButtonsActionOnClick() {
        addEmployeeButton.setOnMouseClicked(action -> {
            isUpdateOperation = false;
            setUpPopUpFXML("Adding");
        });

        updateEmployeeButton.setOnMouseClicked(action -> {
            ObservableList<Employee> employee = employeeTable.getSelectionModel().getSelectedItems();
            if(employee.isEmpty()) {
                //TODO
            } else {
                isUpdateOperation = true;
                setUpPopUpFXML("Updating");

                Employee employeeToUpdate = employee.get(0);

                lastNameField.setText(employeeToUpdate.getLast_name());
                positionField.setText(employeeToUpdate.getPosition());
                birthDateField.setText(employeeToUpdate.getBirth_date().toString());
                hireDateField.setText(employeeToUpdate.getHire_date().toString());
                departmentNumberField.setText(employeeToUpdate.getDepartment_number().toString());
                salaryField.setText(employeeToUpdate.getSalary().toString());
            }
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

        menuButton.setOnMouseClicked(action -> {
            //TODO
        });

    }

    private void setUpPopUpFXML(String title) {
        try {
            double mainX = mainStage.getX();
            double mainY = mainStage.getY();
            SpringFXMLLoader loader = context.getBean(SpringFXMLLoader.class);
            Parent parent = loader.load("/ru/dev1art/ems/PopUpController.fxml");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), parent);
            st.setInterpolator(Interpolator.EASE_BOTH);
            st.setFromX(0);
            st.setFromY(0);
            Stage addStage = new Stage();
            addStage.setTitle(title);
            addStage.initModality(Modality.NONE);
            addStage.initStyle(StageStyle.TRANSPARENT);
            addStage.initOwner(mainStage);
            addStage.setResizable(false);
            Scene scene = new Scene(parent, 150, 350);
            scene.setFill(Color.TRANSPARENT);
            addStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    addStage.close();
                }
            });
            addStage.setScene(scene);
            addStage.show();
            addStage.setX(mainX - 150 - 5);
            addStage.setY(mainY);

            submitButton.setOnMouseClicked(action -> {
                if(isUpdateOperation){
                    addEmployee();
                } else {
                    //TODO
                    updateEmployee();
                }
                refreshTable();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
