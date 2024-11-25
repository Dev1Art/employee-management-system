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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MainController implements Initializable {
    @FXML @Getter @Setter TableView<EmployeeDTO> employeeTable;
    @FXML TableColumn<EmployeeDTO, Integer> idColumn;
    @FXML TableColumn<EmployeeDTO, String> lastNameColumn;
    @FXML TableColumn<EmployeeDTO, String> positionColumn;
    @FXML TableColumn<EmployeeDTO, LocalDate> birthDateColumn;
    @FXML TableColumn<EmployeeDTO, LocalDate> hireDateColumn;
    @FXML TableColumn<EmployeeDTO, Integer> departmentNumberColumn;
    @FXML TableColumn<EmployeeDTO, BigDecimal> salaryColumn;
    @FXML @Getter Button menuButton;
    @FXML @Setter @Getter Button addEmployeeButton;
    @FXML @Setter @Getter Button updateEmployeeButton;
    @FXML @Setter @Getter Button deleteEmployeeButton;
    @FXML @Setter @Getter Button refreshTableButton;
    @FXML @Setter @Getter Button languageChangerButton;
    @FXML @Setter @Getter Button exitButton;
    @Setter Stage mainStage;
    @Autowired @Setter EmployeeService employeeService;
    @Autowired ApplicationContext applicationContext;
    @Autowired SpringFXMLLoader springFXMLLoader;
    @Setter PopUpController popUpController;
    @Setter MenuController menuController;
    boolean isEnglishLocale = true;
    static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    static final Marker DATA_MARKER = MarkerFactory.getMarker("DATA");

    /**
     * Initializes the MainController. This method is called after the FXML
     * file has been loaded and is used to set up the table properties,
     * load employee data, and add action listeners to buttons.
     *
     * @param location  The URL location of the FXML file that was loaded.
     * @param resources The ResourceBundle used to localize the FXML file.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info(UI_MARKER, "Initializing MainController");
        try {
            setUpTableProperties();
            loadEmployeeData();
            addButtonsActionOnClick();
        } catch (Exception exception) {
            log.error("Error during MainController initialization: {}", exception.getMessage());
        }
    }

    /**
     * Sets up the properties for the table view, including binding the
     * columns to the appropriate properties of the EmployeeDTO.
     * This method also enables sorting for each column.
     */
    private void setUpTableProperties() {
        log.info(DATA_MARKER, "Binding tableview properties");
        try {
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
        } catch (Exception exception) {
            log.error("Error during tableview properties binding: {}", exception.getMessage());
        }
    }

    /**
     * Loads the employee data from the EmployeeService and populates
     * the table view with the retrieved EmployeeDTO objects.
     */
    public void loadEmployeeData() {
        log.info(DATA_MARKER, "Loading data to tableview");
        try {
            ObservableList<EmployeeDTO> employees = FXCollections.observableArrayList(employeeService.getAllEmployees());
            employeeTable.setItems(employees);
        } catch (Exception exception) {
            log.error("Error during tableview data loading: {}", exception.getMessage());
        }
    }

    /**
     * Refreshes the employee table by clearing the current items and
     * reloading the data from the EmployeeService.
     */
    public void refreshTable() {
        log.info(UI_MARKER, "Refreshing table");
        employeeTable.getItems().clear();
        loadEmployeeData();
    }

    /**
     * Adds action listeners to the buttons in the user interface,
     * defining the behavior for adding, updating, deleting, refreshing
     * the table, changing the language, and exiting the application.
     */
    protected void addButtonsActionOnClick() {
        log.info(DATA_MARKER, "Setting up actions on click for buttons");
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

    /**
     * Populates the employee table with a list of EmployeeDTO objects.
     *
     * @param employees The list of EmployeeDTO objects to be displayed in the table.
     */
    protected void populateEmployeeTableFromList(List<EmployeeDTO> employees) {
        log.info("Populating tableview");
        employeeTable.getItems().clear();
        employeeTable.getItems().addAll(employees);
    }

    /**
     * Sets up and displays the menu pop-up window.
     */
    private void setUpMenuPopUpFXML() {
        log.info(UI_MARKER, "Loading MenuController.fxml");
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
            Scene scene = new Scene(parent, 200, 178);
            log.info(UI_MARKER, "Loading resource menuFxmlStyle.css");
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
        } catch (IOException exception) {
            log.error("Error during menu resources loading: {}", exception.getMessage());
        }
    }

    /**
     * Sets up and displays the form pop-up window for adding or editing
     * an employee.
     *
     * @param isEditingMode Indicates whether the pop-up is for editing
     *                      an existing employee (true) or adding a new
     *                      employee (false).
     */
    private void setUpFormPopUpFXML(boolean isEditingMode) {
        log.info(UI_MARKER, "Loading PopUpController.fxml");
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
            log.info(UI_MARKER, "Loading resource popupFxmlStyle.css");
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
        } catch (IOException exception) {
            log.error("Error during popup resources loading: {}", exception.getMessage());
        }
    }

    /**
     * Changes the language of the application by updating the text
     * properties of the UI elements based on the current locale.
     */
    public void changeLanguage() {
        log.info(UI_MARKER, "Binding text properties for elements in MainController.fxml");
        try {
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
        } catch (Exception exception) {
            log.error("Error during properties binding for MainController.fxml: {}", exception.getMessage());
        }
    }
}


