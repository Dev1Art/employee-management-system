package ru.dev1art.ems.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import ru.dev1art.ems.util.lang.I18NUtil;
import ru.dev1art.ems.util.lang.LocaleChangeListener;
import ru.dev1art.ems.util.lang.LocalizationManager;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Dev1Art
 * @project EMS
 * @date 23.11.2024
 */

@Component
public class PopUpController implements Initializable, LocaleChangeListener {
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
    @Autowired
    private EmployeeService employeeService;
    @Setter
    private MainController mainController;
    @Setter
    private EmployeeDTO employeeToUpdate;
    @Setter
    private boolean isEditingMode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        LocalizationManager.getInstance().addLocaleChangeListener(this);
        changeLanguage();

        submitButton.setOnMouseClicked(action -> handleSubmit());
    }
    protected void addEmployee() {
        EmployeeDTO newEmployee = employeeService.fromTextToDTO(
                lastNameField.getText(),
                positionField.getText(),
                birthDateField.getText(),
                hireDateField.getText(),
                departmentNumberField.getText(),
                salaryField.getText()
        );
        employeeService.saveEmployee(newEmployee);
        mainController.refreshTable();
    }

    protected void updateEmployee() {
        if(employeeToUpdate != null) {
            EmployeeDTO updatedEmployee = employeeService.mergeDTOs(
                    employeeToUpdate,
                    employeeService.fromTextToDTO(
                            lastNameField.getText(),
                            positionField.getText(),
                            birthDateField.getText(),
                            hireDateField.getText(),
                            departmentNumberField.getText(),
                            salaryField.getText()
                    ));
            employeeService.saveEmployee(updatedEmployee);
            mainController.refreshTable();
        }
    }

    protected void populateTextFieldsForEditing(EmployeeDTO employeeDTO) {
        lastNameField.setText(employeeDTO.lastName());
        positionField.setText(employeeDTO.position());
        birthDateField.setText(employeeDTO.birthDate().toString());
        hireDateField.setText(employeeDTO.hireDate().toString());
        departmentNumberField.setText(employeeDTO.departmentNumber().toString());
        salaryField.setText(employeeDTO.salary().toString());

        this.employeeToUpdate = employeeDTO;
    }

    private void handleSubmit() {
        if (isEditingMode) {
            updateEmployee();
        } else {
            addEmployee();
        }
    }


    public void changeLanguage() {
        submitButton.textProperty().bind(I18NUtil.createStringBinding("submitButton"));
        popUpTitle.textProperty().bind(I18NUtil.createStringBinding("popUpTitle"));
        lastNameLabel.textProperty().bind(I18NUtil.createStringBinding("lastNameLabel"));
        positionLabel.textProperty().bind(I18NUtil.createStringBinding("positionLabel"));
        birthDateLabel.textProperty().bind(I18NUtil.createStringBinding("birthDateLabel"));
        hireDateLabel.textProperty().bind(I18NUtil.createStringBinding("hireDateLabel"));
        departmentNumberLabel.textProperty().bind(I18NUtil.createStringBinding("departmentNumberLabel"));
        salaryLabel.textProperty().bind(I18NUtil.createStringBinding("salaryLabel"));
    }

    @Override
    public void localeChanged(Locale newLocale) {
        changeLanguage();
    }
}
