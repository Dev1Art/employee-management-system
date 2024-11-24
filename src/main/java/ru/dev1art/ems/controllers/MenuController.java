package ru.dev1art.ems.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.*;
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

@Component
public class MenuController implements Initializable, LocaleChangeListener {
    @FXML
    private Label menuLabel;
    @FXML
    private ComboBox<String> shortcutsComboBox;
    @FXML
    private TextField valueField;
    @FXML
    private Button findButton;
    private final Map<String, ShortcutProperties> shortcutOperations = new HashMap<>();
    @Setter
    private MainController mainController;
    @Autowired
    private EmployeeService employeeService;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        LocalizationManager.getInstance().addLocaleChangeListener(this);
        changeLanguage();

        shortcutsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateInputFieldState(newValue);
        });

        populateShortcutsComboBox();
        addButtonsActionOnClick();
    }

    @Override
    public void localeChanged(Locale newLocale) {
        changeLanguage();
    }

    private void changeLanguage() {
        menuLabel.textProperty().bind(I18NUtil.createStringBinding("menuLabel"));
        findButton.textProperty().bind(I18NUtil.createStringBinding("findButton"));
        shortcutsComboBox.promptTextProperty().bind(I18NUtil.createStringBinding("comboBoxTitle"));
        populateShortcutsComboBox();
    }

    private void populateShortcutsComboBox() {
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
    }

    private void addButtonsActionOnClick() {
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

    private void updateInputFieldState(String selectedShortcut) {
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

    private record ShortcutProperties(String promptText, Runnable operation, boolean disabledValueField){}

    private void findYounger() {
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer deptNo = null;
        Integer age = null;
        if (matcher.find()) {
            try {
                deptNo = Integer.parseInt(matcher.group(1));
                age = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                //todo
            }
        } else {
            //todo
        }
        List<EmployeeDTO> employees = employeeService.getEmployeesInDepartmentYoungerThan(deptNo, age);
        mainController.populateEmployeeTableFromList(employees);
    }

    private void findMinSalary() {
        List<EmployeeDTO> employees = employeeService.getEmployeesWithMinSalary();
        mainController.populateEmployeeTableFromList(employees);
    }

    private void findHighestSalary() {
        List<EmployeeDTO> employees = employeeService.getEmployeesWithMaxSalary();
        mainController.populateEmployeeTableFromList(employees);
    }

    private void findWorkingSince() {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer years = null;
        if (matcher.find()) {
            try {
                years = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                //todo
            }
        } else {
            //todo
        }
        List<EmployeeDTO> employees = employeeService.getLongTermEmployees(years);
        mainController.populateEmployeeTableFromList(employees);
    }

    private void findOlderThan() {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        Integer years = null;
        if (matcher.find()) {
            try {
                years = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                //todo
            }
        } else {
            //todo
        }
        List<EmployeeDTO> employees = employeeService.findOldEmployees(years);
        mainController.populateEmployeeTableFromList(employees);
    }

    private void getAgeOfEmployee() {
        Integer employeeID = Integer.parseInt(valueField.getText());
        EmployeeDTO employee = employeeService.findById(employeeID);
        valueField.setText(I18NUtil.createStringBinding("shortcut.getCurrentAge.answer").get() + ": " + employeeService.getCurrentAge(employee).toString());
        valueField.setEditable(false);
    }

    private void getAgeOfEmployeeWhenHired() {
        Integer employeeID = Integer.parseInt(valueField.getText());
        EmployeeDTO employee = employeeService.findById(employeeID);
        valueField.setText(I18NUtil.createStringBinding("shortcut.getAgeAtHire.answer").get() + ": " + employeeService.getAgeAtHire(employee).toString());
        valueField.setEditable(false);
    }

    private void increaseSalaryForLongTermEmployees() {
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(valueField.getText());
        BigDecimal percentageIncrease = null;
        Integer years = null;
        if (matcher.find()) {
            try {
                percentageIncrease = new BigDecimal(matcher.group(1));
                years = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                //todo
            }
        } else {
            //todo
        }
        List<EmployeeDTO> employees = employeeService.increaseSalaryForLongTermEmployees(percentageIncrease, years);
        mainController.populateEmployeeTableFromList(employees);
    }
}
