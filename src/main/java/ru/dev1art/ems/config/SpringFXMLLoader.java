package ru.dev1art.ems.config;

import javafx.fxml.FXMLLoader;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for loading FXML files in a Spring context.
 * This class integrates JavaFX's FXMLLoader with Spring's ApplicationContext
 * to allow for dependency injection of controllers defined in FXML files.
 *
 * @author Dev1Art
 * @project EMS
 * @date 10.11.2024
 */

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpringFXMLLoader {
    static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    @Autowired @Setter ApplicationContext applicationContext;

    /**
     * Loads an FXML file and returns the corresponding controller instance.
     * This method uses the FXMLLoader to load the FXML resource and sets the controller factory
     * to retrieve beans from the Spring application context.
     *
     * @param fxml The path to the FXML file to be loaded.
     * @param <T>  The type of the controller that will be loaded from the FXML.
     * @return An instance of the controller defined in the FXML file.
     * @throws IOException if there is an error loading the FXML file or the resource is not found.
     */
    public <T> T load(String fxml) throws IOException {
        log.debug(UI_MARKER, "Loading FXML: {}", fxml);

        URL fxmlResource = getClass().getResource(fxml);
        if (fxmlResource == null) {
            String message = "FXML resource not found: " + fxml;
            log.error(message);
            throw new IOException(message);
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlResource);
            loader.setControllerFactory(applicationContext::getBean);
            T loadedObject = loader.load();
            log.debug(UI_MARKER, "FXML loaded successfully: {}", fxml);
            return loadedObject;
        } catch (IOException exception) {
            log.error("Error loading FXML {}: ", fxml, exception);
            throw exception;
        }
    }
}
