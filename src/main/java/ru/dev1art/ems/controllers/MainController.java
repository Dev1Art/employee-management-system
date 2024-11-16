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
    private Label popUpTitle;
    @FXML
    private Button submitButton;
    @FXML
    private TextField lastNameField;
    @FXML
    private Label lastNameLabel;
    @FXML
    private TextField positionField;
    @FXML
    private Label positionLabel;
    @FXML
    private TextField birthDateField;
    @FXML
    private Label birthDateLabel;
    @FXML
    private TextField hireDateField;
    @FXML
    private Label hireDateLabel;
    @FXML
    private TextField departmentNumberField;
    @FXML
    private Label departmentNumberLabel;
    @FXML
    private TextField salaryField;
    @FXML
    private Label salaryLabel;
    @Setter
    private Stage mainStage;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ApplicationContext context;
    private boolean isEnglishLocale = true;

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
        Employee employee = new Employee();
        fromTextFieldsToEntity(employee);
        employeeService.saveEmployee(employee);
    }

    private void updateEmployee() {
        Employee employeeToSave = employeeTable.getSelectionModel().getSelectedItems().get(0);
        fromTextFieldsToEntity(employeeToSave);
        employeeService.saveEmployee(employeeToSave);
    }

    private void fromTextFieldsToEntity(Employee employeeToSave) {
        try {
            employeeToSave.setLastName(lastNameField.getText());
            employeeToSave.setPosition(positionField.getText());
            employeeToSave.setBirthDate(LocalDate.parse(birthDateField.getText()));
            employeeToSave.setHireDate(LocalDate.parse(hireDateField.getText()));
            employeeToSave.setDepartmentNumber(Integer.parseInt(departmentNumberField.getText()));
            employeeToSave.setSalary(new BigDecimal(salaryField.getText()));
        }  catch (NumberFormatException | DateTimeParseException | NullPointerException exception) {
            //TODO
        }
    }

    private void addButtonsActionOnClick() {
        addEmployeeButton.setOnMouseClicked(action -> setUpPopUpFXML(false));

        updateEmployeeButton.setOnMouseClicked(action -> {
            ObservableList<Employee> employees = employeeTable.getSelectionModel().getSelectedItems();
            if (employees.isEmpty()) {
                //TODO
            } else {
                setUpPopUpFXML(true);
                Employee employee = employees.get(0);
                populateTextFieldsForEditing(employee);
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
            lastNameColumn.textProperty().bind(I18NUtil.createStringBinding("lastNameColumn"));
            positionColumn.textProperty().bind(I18NUtil.createStringBinding("positionColumn"));
            birthDateColumn.textProperty().bind(I18NUtil.createStringBinding("birthDateColumn"));
            hireDateColumn.textProperty().bind(I18NUtil.createStringBinding("hireDateColumn"));
            departmentNumberColumn.textProperty().bind(I18NUtil.createStringBinding("departmentNumberColumn"));
            salaryColumn.textProperty().bind(I18NUtil.createStringBinding("salaryColumn"));
        });

        exitButton.setOnMouseClicked(action -> System.exit(0));

        menuButton.setOnMouseClicked(action -> {
            //TODO
        });
    }

    private void populateTextFieldsForEditing(Employee employee) {
        lastNameField.setText(employee.getLastName());
        positionField.setText(employee.getPosition());
        birthDateField.setText(employee.getBirthDate().toString());
        hireDateField.setText(employee.getHireDate().toString());
        departmentNumberField.setText(employee.getDepartmentNumber().toString());
        salaryField.setText(employee.getSalary().toString());
    }

    private void setUpPopUpFXML(boolean isEditingMode) {
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

            if(isEnglishLocale) {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(0));
            } else {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(1));
            }

            addStage.titleProperty().bind(I18NUtil.createStringBinding("titleName"));
            submitButton.textProperty().bind(I18NUtil.createStringBinding("submitButton"));
            popUpTitle.textProperty().bind(I18NUtil.createStringBinding("popUpTitle"));
            lastNameLabel.textProperty().bind(I18NUtil.createStringBinding("lastNameLabel"));
            positionLabel.textProperty().bind(I18NUtil.createStringBinding("positionLabel"));
            birthDateLabel.textProperty().bind(I18NUtil.createStringBinding("birthDateLabel"));
            hireDateLabel.textProperty().bind(I18NUtil.createStringBinding("hireDateLabel"));
            departmentNumberLabel.textProperty().bind(I18NUtil.createStringBinding("departmentNumberLabel"));
            salaryLabel.textProperty().bind(I18NUtil.createStringBinding("salaryLabel"));

            addStage.initModality(Modality.NONE);
            addStage.initStyle(StageStyle.TRANSPARENT);
            addStage.initOwner(mainStage);
            addStage.setResizable(false);
            Scene scene = new Scene(parent, 150, 360);
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

            submitButton.setOnMouseClicked(action -> handleSubmit(isEditingMode));
        } catch (IOException e) {
            //TODO
        }
    }

    private void handleSubmit(boolean isEditingMode) {
        if(isEditingMode) {
            updateEmployee();
            //TODO
        } else {
            addEmployee();
            //TODO
        }
        refreshTable();
    }
}
