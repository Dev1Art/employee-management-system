package ru.dev1art.ems.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PopUpController implements Initializable, LocaleChangeListener {
    @FXML @Getter @Setter Label popUpTitle;
    @FXML @Getter @Setter Button submitButton;
    @FXML @Getter @Setter TextField lastNameField;
    @FXML @Getter @Setter Label lastNameLabel;
    @FXML @Getter @Setter TextField positionField;
    @FXML @Getter @Setter Label positionLabel;
    @FXML @Getter @Setter TextField birthDateField;
    @FXML @Getter @Setter Label birthDateLabel;
    @FXML @Getter @Setter TextField hireDateField;
    @FXML @Getter @Setter Label hireDateLabel;
    @FXML @Getter @Setter TextField departmentNumberField;
    @FXML @Getter @Setter Label departmentNumberLabel;
    @FXML @Getter @Setter TextField salaryField;
    @FXML @Getter @Setter Label salaryLabel;
    @Autowired @Setter EmployeeService employeeService;
    @Setter @Getter MainController mainController;
    @Setter @Getter EmployeeDTO employeeToUpdate;
    @Setter boolean isEditingMode;
    static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    static final Marker DATA_MARKER = MarkerFactory.getMarker("DATA");
    static final Marker SERVICE_MARKER = MarkerFactory.getMarker("SERVICE");

    /**
     * Initializes the PopUpController. This method is called after the FXML
     * file has been loaded and is used to set up localization and event handlers.
     *
     * @param url The URL location of the FXML file that was loaded.
     * @param resourceBundle The ResourceBundle used to localize the FXML file.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info(UI_MARKER, "Initializing PopUpController");
        LocalizationManager.getInstance().addLocaleChangeListener(this);
        changeLanguage();
        submitButton.setOnMouseClicked(action -> handleSubmit());
    }

    /**
     * Adds a new employee using the data from the input fields.
     * Logs the operation and handles any exceptions that may occur.
     */
    protected void addEmployee() {
        log.debug(SERVICE_MARKER, "Adding new employee");
        try {
            EmployeeDTO newEmployee = employeeService.fromTextToDTO(
                    lastNameField.getText(), positionField.getText(), birthDateField.getText(),
                    hireDateField.getText(), departmentNumberField.getText(), salaryField.getText()
            );

            if (newEmployee == null) {
                log.error(SERVICE_MARKER, "Failed to create EmployeeDTO from input fields.");
                return;
            }

            employeeService.saveEmployee(newEmployee);
            mainController.refreshTable();
            log.info(DATA_MARKER, "New employee added: {}", newEmployee);
        } catch (Exception e) {
            log.error("Error adding employee:", e);
        }
    }

    /**
     * Updates the existing employee with new data from the input fields.
     * If the employee to update is null, logs a warning.
     */
    protected void updateEmployee() {
        log.debug(SERVICE_MARKER, "Updating employee");
        if (employeeToUpdate != null) {
            try {
                EmployeeDTO updatedEmployee = employeeService.mergeDTOs(employeeToUpdate, employeeService.fromTextToDTO(
                        lastNameField.getText(), positionField.getText(), birthDateField.getText(),
                        hireDateField.getText(), departmentNumberField.getText(), salaryField.getText()
                ));
                employeeService.saveEmployee(updatedEmployee);
                mainController.refreshTable();
                log.info(DATA_MARKER, "Employee updated: {}", updatedEmployee);
            } catch (Exception e) {
                log.error("Error updating employee:", e);
            }
        } else {
            log.warn(DATA_MARKER, "Cannot update employee: employeeToUpdate is null");
        }
    }

    /**
     * Populates the text fields with the details of an employee for editing.
     *
     * @param employeeDTO The EmployeeDTO object containing the employee's information.
     */
    protected void populateTextFieldsForEditing(EmployeeDTO employeeDTO) {
        log.debug(UI_MARKER, "Populating text fields for editing employee: {}", employeeDTO);

        lastNameField.setText(employeeDTO.lastName());
        positionField.setText(employeeDTO.position());
        birthDateField.setText(employeeDTO.birthDate().toString());
        hireDateField.setText(employeeDTO.hireDate().toString());
        departmentNumberField.setText(employeeDTO.departmentNumber().toString());
        salaryField.setText(employeeDTO.salary().toString());

        this.employeeToUpdate = employeeDTO;
    }

    /**
     * Handles the submission of the form. Depending on the editing mode,
     * it either adds a new employee or updates an existing employee.
     */
    private void handleSubmit() {
        log.debug(UI_MARKER, "Handling form submission. Editing mode: {}", isEditingMode);

        if (isEditingMode) {
            updateEmployee();
        } else {
            addEmployee();
        }
    }

    /**
     * Changes the language of the UI elements by binding text properties
     * to localized strings.
     */
    public void changeLanguage() {
        log.debug(UI_MARKER, "Changing UI language");

        submitButton.textProperty().bind(I18NUtil.createStringBinding("submitButton"));
        popUpTitle.textProperty().bind(I18NUtil.createStringBinding("popUpTitle"));
        lastNameLabel.textProperty().bind(I18NUtil.createStringBinding("lastNameLabel"));
        positionLabel.textProperty().bind(I18NUtil.createStringBinding("positionLabel"));
        birthDateLabel.textProperty().bind(I18NUtil.createStringBinding("birthDateLabel"));
        hireDateLabel.textProperty().bind(I18NUtil.createStringBinding("hireDateLabel"));
        departmentNumberLabel.textProperty().bind(I18NUtil.createStringBinding("departmentNumberLabel"));
        salaryLabel.textProperty().bind(I18NUtil.createStringBinding("salaryLabel"));
    }

    /**
     * Called when the locale changes. Updates the language of the UI elements.
     *
     * @param newLocale The new locale that has been set.
     */
    @Override
    public void localeChanged(Locale newLocale) {
        changeLanguage();
    }
}
