package ru.dev1art.ems.config;

/**
 * @author Dev1Art
 * @project EMS
 * @date 10.11.2024
 */

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@Component
public class SpringFXMLLoader {

    @Autowired
    private ApplicationContext applicationContext;

    public <T> T load(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }
}
