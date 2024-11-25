package ru.dev1art.ems.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.*;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dev1Art
 * @project EMS
 * @date 23.11.2024
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class MenuController implements Initializable, LocaleChangeListener {
    @FXML @Setter Label menuLabel;
    @FXML @Setter ComboBox<String> shortcutsComboBox;
    @FXML @Getter @Setter TextField valueField;
    @FXML @Getter @Setter Button findButton;
    final Map<String, ShortcutProperties> shortcutOperations = new HashMap<>();
    @Getter @Setter MainController mainController;
    @Autowired EmployeeService employeeService;
    static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    static final Marker DATA_MARKER = MarkerFactory.getMarker("DATA");
    static final Marker SERVICE_MARKER = MarkerFactory.getMarker("SERVICE");

    /**
     * Initializes the MenuController. This method is called after the FXML
     * file has been loaded and is used to set up localization, populate
     * the shortcuts combo box, and add action listeners to buttons.
     *
     * @param url The URL location of the FXML file that was loaded.
     * @param resourceBundle The ResourceBundle used to localize the FXML file.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info(UI_MARKER, "Initializing MenuController");
        try {
            LocalizationManager.getInstance().addLocaleChangeListener(this);
            changeLanguage();

            shortcutsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                valueField.clear();
                updateInputFieldState(newValue);
            });
            populateShortcutsComboBox();
            addButtonsActionOnClick();
        } catch (Exception exception) {
            log.error("Error during MenuController initialization: {}", exception.getMessage());
        }
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

    /**
     * Changes the language of the UI elements by binding text properties
     * to localized strings.
     */
    private void changeLanguage() {
        log.info(UI_MARKER, "Binding text properties for elements in MenuController.fxml");
        try {
            menuLabel.textProperty().bind(I18NUtil.createStringBinding("menuLabel"));
            findButton.textProperty().bind(I18NUtil.createStringBinding("findButton"));
            shortcutsComboBox.promptTextProperty().bind(I18NUtil.createStringBinding("comboBoxTitle"));
        } catch (Exception exception) {
            log.error("Error during properties binding for MenuController.fxml: {}", exception.getMessage());
        }
    }

    /**
     * Populates the shortcuts combo box with available shortcut operations
     * and their corresponding properties.
     */
    private void populateShortcutsComboBox() {
        log.info(DATA_MARKER, "Populating shortcuts for comboBox element");
        try {
            shortcutsComboBox.getItems().clear();
            shortcutOperations.clear();
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.findYounger").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.findYounger.prompt").get(),
                            this::findYounger, false));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.minSalary").get(),
                    new ShortcutProperties("", this::findMinSalary, true));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.highestSalary").get(),
                    new ShortcutProperties("", this::findHighestSalary, true));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.workingSince").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.workingSince.prompt").get(),
                            this::findWorkingSince, false));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.olderThan").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.olderThan.prompt").get(),
                            this::findOlderThan, false));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.getCurrentAge").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.getCurrentAge.prompt").get(),
                            this::getAgeOfEmployee, false));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.getAgeAtHire").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.getAgeAtHire.prompt").get(),
                            this::getAgeOfEmployeeWhenHired, false));
            shortcutOperations.put(I18NUtil.createStringBinding("shortcut.increaseSalary").get(),
                    new ShortcutProperties(I18NUtil.createStringBinding("shortcut.increaseSalary.prompt").get(),
                            this::increaseSalaryForLongTermEmployees, false));
            shortcutsComboBox.getItems().addAll(shortcutOperations.keySet());
        } catch (Exception exception) {
            log.error("Error during comboBox populating: {}", exception.getMessage());
        }
    }

    /**
     * Sets up action listeners for buttons in the UI, defining the behavior
     * for finding employees based on selected shortcuts.
     */
    private void addButtonsActionOnClick() {
        log.info(DATA_MARKER, "Setting up actions on click for buttons");
        findButton.setOnMouseClicked(action -> {
            String selectedShortcut = shortcutsComboBox.getValue();
            if (selectedShortcut != null) {
                Runnable operation = shortcutOperations.get(selectedShortcut).operation();
                if (operation != null) {
                    operation.run();
                }
            }
        });
    }

    /**
     * Updates the state of the input field based on the selected shortcut.
     *
     * @param selectedShortcut The shortcut selected in the combo box.
     */
    private void updateInputFieldState(String selectedShortcut) {
        log.info(UI_MARKER, "Updating textField state");
        ShortcutProperties properties = shortcutOperations.get(selectedShortcut);
        if (properties != null) {
            valueField.setPromptText(properties.promptText());
            valueField.setDisable(properties.disabledValueField());
            valueField.setEditable(!properties.disabledValueField());

        } else {
            valueField.setDisable(true);
            valueField.setEditable(false);
        }
    }

    /**
     * Represents the properties of a shortcut, including its prompt text
     * and the operation to execute when selected.
     */
    private record ShortcutProperties(String promptText, Runnable operation, boolean disabledValueField){}

    /**
     * Finds employees in a specified department who are younger than a given age.
     */
    protected void findYounger() {
        log.debug(SERVICE_MARKER, "Finding employees in department younger than entered value");
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer deptNo = null;
        Integer age = null;
        if (matcher.find()) {
            try {
                deptNo = Integer.parseInt(matcher.group(1));
                age = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                log.error("Error during Integer parsing: {}", exception.getMessage());
            }
        } else {
            log.warn("No match found for entered data");
        }
        List<EmployeeDTO> employees = employeeService.getEmployeesInDepartmentYoungerThan(deptNo, age);
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Found {} employee(s) younger than {}", employees.size(), age);
    }

    /**
     * Finds and displays employees with the minimum salary.
     */
    protected void findMinSalary() {
        log.debug(SERVICE_MARKER, "Finding employees with minimum salary");
        List<EmployeeDTO> employees = employeeService.getEmployeesWithMinSalary();
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Found {} employees in department younger than entered value", employees.size());
    }

    /**
     * Finds and displays employees with the highest salary.
     */
    protected void findHighestSalary() {
        log.debug(SERVICE_MARKER, "Finding employees with highest salary");
        List<EmployeeDTO> employees = employeeService.getEmployeesWithMaxSalary();
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Found {} employees with highest salary", employees.size());
    }

    /**
     * Finds employees who have been working for a specified number of years.
     */
    protected void findWorkingSince() {
        log.debug(SERVICE_MARKER, "Finding long-term employees");
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer years = null;
        if (matcher.find()) {
            try {
                years = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("Invalid input for years: {}", valueField.getText(), e);
                return;
            }
        } else {
            log.error("Invalid input format for years: {}", valueField.getText());
            return;
        }

        List<EmployeeDTO> employees = employeeService.getLongTermEmployees(years);
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Found {} long-term employees ({} years)", employees.size(), years);
    }

    /**
     * Finds and displays employees older than a specified age.
     */
    protected void findOlderThan() {
        log.debug(SERVICE_MARKER, "Finding employees older than a specified age");
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer years = null;
        if (matcher.find()) {
            try {
                years = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("Invalid input for age: {}", valueField.getText(), e);
                return;
            }
        } else {
            log.error("Invalid input format for age: {}", valueField.getText());
            return;
        }

        List<EmployeeDTO> employees = employeeService.findOldEmployees(years);
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Found {} employees older than {} years", employees.size(), years);
    }

    /**
     * Gets and displays the current age of an employee based on their ID.
     */
    protected void getAgeOfEmployee() {
        log.debug(SERVICE_MARKER, "Getting current age of employee");
        try {
            Integer employeeID = Integer.parseInt(valueField.getText());
            EmployeeDTO employee = employeeService.findById(employeeID);

            if (employee == null) {
                log.warn(DATA_MARKER, "Employee not found for ID: {}", employeeID);
                valueField.setText("Employee not found");
                return;
            }

            Integer age = employeeService.getCurrentAge(employee);
            valueField.setText(I18NUtil.createStringBinding("shortcut.getCurrentAge.answer").get() + ": " + age);
            valueField.setEditable(false);
            log.info(DATA_MARKER, "Current age of employee {} is {}", employeeID, age);

        } catch (NumberFormatException e) {
            log.error("Invalid employee ID: {}", valueField.getText(), e);
        }
    }

    /**
     * Gets and displays the age of an employee at the time of hire based on their ID.
     */
    protected void getAgeOfEmployeeWhenHired() {
        log.debug(SERVICE_MARKER, "Getting age of employee at hire date");
        try {
            Integer employeeID = Integer.parseInt(valueField.getText());
            EmployeeDTO employee = employeeService.findById(employeeID);
            if (employee == null) {
                log.warn(DATA_MARKER, "Employee not found for ID: {}", employeeID);
                valueField.setText("Employee not found");
                return;
            }

            Integer ageAtHire = employeeService.getAgeAtHire(employee);
            valueField.setText(I18NUtil.createStringBinding("shortcut.getAgeAtHire.answer").get() + ": " + ageAtHire);
            valueField.setEditable(false);
            log.info(DATA_MARKER, "Age at hire date for employee {} is {}", employeeID, ageAtHire);

        } catch (NumberFormatException e) {
            log.error("Invalid employee ID: {}", valueField.getText(), e);
        }
    }

    /**
     * Increases the salary of long-term employees based on a specified
     * percentage increase and number of years.
     */
    protected void increaseSalaryForLongTermEmployees() {
        log.debug(SERVICE_MARKER, "Increasing salary for long-term employees");
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        BigDecimal percentageIncrease = null;
        Integer years = null;
        if (matcher.find()) {
            try {
                percentageIncrease = new BigDecimal(matcher.group(1));
                years = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                log.error("Invalid input for salary increase: {}", valueField.getText(), exception);
                return;
            }
        } else {
            log.error("Invalid input format for salary increase: {}", valueField.getText());
            return;
        }

        List<EmployeeDTO> employees = employeeService.increaseSalaryForLongTermEmployees(percentageIncrease, years);
        mainController.populateEmployeeTableFromList(employees);
        log.info(DATA_MARKER, "Increased salary for {} long-term employees ({} years, {}% increase)",
                employees.size(), years, percentageIncrease);
    }
}
