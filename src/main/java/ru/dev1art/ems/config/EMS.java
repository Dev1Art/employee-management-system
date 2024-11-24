package ru.dev1art.ems.config;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.dev1art.ems.controllers.MainController;

import java.util.Objects;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */

@SpringBootApplication
@ComponentScan(basePackages = "ru.dev1art.ems")
@EntityScan(basePackages = "ru.dev1art.ems.domain.model")
@EnableJpaRepositories(basePackages = "ru.dev1art.ems.repos")
public class EMS extends Application {
    private ConfigurableApplicationContext configurableApplicationContext;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        configurableApplicationContext = SpringApplication.run(EMS.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
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
    }

    @Override
    public void stop() throws Exception {
        configurableApplicationContext.close();
    }
}
