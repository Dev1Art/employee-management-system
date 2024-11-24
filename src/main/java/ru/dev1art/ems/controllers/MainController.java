package ru.dev1art.ems.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.dev1art.ems.config.SpringFXMLLoader;
import ru.dev1art.ems.domain.dto.EmployeeDTO;
import ru.dev1art.ems.services.EmployeeService;
import ru.dev1art.ems.util.lang.I18NUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */
@Component
@NoArgsConstructor
public class MainController implements Initializable {

    @FXML
    private TableView<EmployeeDTO> employeeTable;
    @FXML
    private TableColumn<EmployeeDTO, Integer> idColumn;
    @FXML
    private TableColumn<EmployeeDTO, String> lastNameColumn;
    @FXML
    private TableColumn<EmployeeDTO, String> positionColumn;
    @FXML
    private TableColumn<EmployeeDTO, LocalDate> birthDateColumn;
    @FXML
    private TableColumn<EmployeeDTO, LocalDate> hireDateColumn;
    @FXML
    private TableColumn<EmployeeDTO, Integer> departmentNumberColumn;
    @FXML
    private TableColumn<EmployeeDTO, BigDecimal> salaryColumn;
    @FXML
    private Button menuButton;
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
    @Setter
    private Stage mainStage;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;
    private PopUpController popUpController;
    private MenuController menuController;
    private boolean isEnglishLocale = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTableProperties();
        loadEmployeeData();
        addButtonsActionOnClick();
    }

    private void setUpTableProperties() {
        idColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.id());
        });
        idColumn.setSortable(true);
        lastNameColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.lastName());
        });
        lastNameColumn.setSortable(true);
        positionColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.position());
        });
        positionColumn.setSortable(true);
        birthDateColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.birthDate());
        });
        birthDateColumn.setSortable(true);
        hireDateColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.hireDate());
        });
        hireDateColumn.setSortable(true);
        departmentNumberColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.departmentNumber());
        });
        departmentNumberColumn.setSortable(true);
        salaryColumn.setCellValueFactory(cellData -> {
            EmployeeDTO employeeDTO = cellData.getValue();
            return new SimpleObjectProperty<>(employeeDTO.salary());
        });
        salaryColumn.setSortable(true);
    }

    private void loadEmployeeData() {
        ObservableList<EmployeeDTO> employees = FXCollections.observableArrayList(employeeService.getAllEmployees());
        employeeTable.setItems(employees);
    }
    public void refreshTable() {
        employeeTable.getItems().clear();
        loadEmployeeData();
    }

    private void addButtonsActionOnClick() {
        addEmployeeButton.setOnMouseClicked(action -> setUpFormPopUpFXML(false));

        updateEmployeeButton.setOnMouseClicked(action -> {
            if (employeeTable.getSelectionModel().getSelectedItems().get(0) != null) {
                setUpFormPopUpFXML(true);
            }
        });

        deleteEmployeeButton.setOnMouseClicked(action -> {
            EmployeeDTO employeeToDelete = employeeTable.getSelectionModel().getSelectedItems().get(0);
            employeeService.deleteEmployee(employeeToDelete.id());
            refreshTable();
        });

        refreshTableButton.setOnMouseClicked(action -> refreshTable());
        languageChangerButton.setOnMouseClicked(event -> {

            if (!isEnglishLocale) {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(0));
                isEnglishLocale = true;
            } else {
                I18NUtil.setLocale(I18NUtil.getSupportedLocales().get(1));
                isEnglishLocale = false;
            }
            changeLanguage();
            if (popUpController != null) {
                popUpController.changeLanguage();
            }
        });

        exitButton.setOnMouseClicked(action -> System.exit(0));

        menuButton.setOnMouseClicked(action -> setUpMenuPopUpFXML());
    }

    protected void populateEmployeeTableFromList(List<EmployeeDTO> employees) {
        employeeTable.getItems().clear();
        employeeTable.getItems().addAll(employees);
    }

    private void setUpMenuPopUpFXML() {
        try {
            double mainX = mainStage.getX();
            double mainY = mainStage.getY();
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            Parent parent = loader.load(Objects.requireNonNull(
                    getClass().getResourceAsStream("/ru/dev1art/ems/MenuController.fxml")));
            menuController = loader.getController();
            menuController.setMainController(this);

            ScaleTransition st = new ScaleTransition(Duration.millis(100), parent);
            st.setInterpolator(Interpolator.EASE_BOTH);
            st.setFromX(0);
            st.setFromY(0);
            Stage menuStage = new Stage();
            menuStage.initModality(Modality.NONE);
            menuStage.initStyle(StageStyle.TRANSPARENT);
            menuStage.initOwner(mainStage);
            menuStage.setResizable(false);
            Scene scene = new Scene(parent, 200, 300);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/ru/dev1art/ems/styles/menuFxmlStyle.css")).toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            menuStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    menuStage.close();
                }
            });
            menuStage.setScene(scene);
            menuStage.show();
            menuStage.setX(mainX + 700 + 5);
            menuStage.setY(mainY);
        } catch (IOException e) {
            //TODO
        }
    }

    private void setUpFormPopUpFXML(boolean isEditingMode) {
        try {
            double mainX = mainStage.getX();
            double mainY = mainStage.getY();
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            Parent parent = loader.load(Objects.requireNonNull(
                    getClass().getResourceAsStream("/ru/dev1art/ems/PopUpController.fxml")));
            popUpController = loader.getController();
            popUpController.setMainController(this);
            popUpController.setEditingMode(isEditingMode);
            if(isEditingMode) {
                EmployeeDTO selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
                if (selectedEmployee != null) {
                    popUpController.populateTextFieldsForEditing(selectedEmployee);
                }
            }
            ScaleTransition st = new ScaleTransition(Duration.millis(100), parent);
            st.setInterpolator(Interpolator.EASE_BOTH);
            st.setFromX(0);
            st.setFromY(0);
            Stage addStage = new Stage();
            addStage.initModality(Modality.NONE);
            addStage.initStyle(StageStyle.TRANSPARENT);
            addStage.initOwner(mainStage);
            addStage.setResizable(false);
            Scene scene = new Scene(parent, 150, 360);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/ru/dev1art/ems/styles/popupFxmlStyle.css")).toExternalForm());
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
        } catch (IOException e) {
            //TODO
        }
    }

    public void changeLanguage() {
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
    }
}


