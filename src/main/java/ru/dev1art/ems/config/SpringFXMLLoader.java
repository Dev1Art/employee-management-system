package ru.dev1art.ems.config;

import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;

/**
 * @author Dev1Art
 * @project EMS
 * @date 10.11.2024
 */

@Slf4j
@Component
public class SpringFXMLLoader {
    private static final Marker UI_MARKER = MarkerFactory.getMarker("UI");
    @Autowired
    private ApplicationContext applicationContext;

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
