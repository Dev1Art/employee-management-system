package ru.dev1art.ems.config;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.dev1art.ems.controllers.MainController;

import java.util.Objects;

/**
 * Main entry point for the EMS application.
 * This class extends the JavaFX Application class and integrates Spring Boot to manage the application context.
 * It handles the initialization of the Spring context, the setup of the JavaFX UI, and application lifecycle events.
 *
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "ru.dev1art.ems")
@EntityScan(basePackages = "ru.dev1art.ems.domain.model")
@EnableJpaRepositories(basePackages = "ru.dev1art.ems.repos")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EMS extends Application {

    static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    static final Marker APPLICATION_MARKER = MarkerFactory.getMarker("APPLICATION");
    ConfigurableApplicationContext configurableApplicationContext;

    /**
     * Main method that serves as the entry point of the application.
     * It initializes the application and launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        log.info(APPLICATION_MARKER, "Starting EMS application");
        Application.launch(args);
    }

    /**
     * Initializes the Spring application context before the JavaFX application starts.
     * This method is called by the JavaFX framework during the application lifecycle.
     *
     * @throws Exception if any error occurs during the initialization of the Spring context.
     */
    @Override
    public void init() throws Exception {
        log.info(APPLICATION_MARKER, "Initializing Spring context");
        configurableApplicationContext = SpringApplication.run(EMS.class);
    }

    /**
     * Starts the JavaFX application and sets up the main stage and scene.
     * This method is called after the init method and is responsible for loading the FXML layout
     * and applying styles.
     *
     * @param stage The primary stage for this application, onto which the application scene can be set.
     * @throws Exception if any error occurs during the setup of the UI.
     */
    @Override
    public void start(Stage stage) throws Exception {
        log.info(UI_MARKER, "Starting UI");
        try {
            SpringFXMLLoader loader = configurableApplicationContext.getBean(SpringFXMLLoader.class);
            Scene scene = new Scene(loader.load("/ru/dev1art/ems/MainController.fxml"), 700, 400);
            scene.getStylesheets().add(Objects.requireNonNull(
                    EMS.class.getResource("/ru/dev1art/ems/styles/mainFxmlStyle.css")).toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            MainController mainController = configurableApplicationContext.getBean(MainController.class);
            mainController.setMainStage(stage);

            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("EMS");
            stage.setScene(scene);
            stage.show();
            log.info(UI_MARKER, "Main stage shown");

        } catch (Exception exception) {
            log.error("Error starting UI:", exception);
        }
    }

    /**
     * Stops the JavaFX application and closes the Spring application context.
     * This method is called when the application is about to exit.
     *
     * @throws Exception if any error occurs during the stopping of the application.
     */
    @Override
    public void stop() throws Exception {
        log.info(APPLICATION_MARKER, "Stopping EMS application");
        configurableApplicationContext.close();
    }
}