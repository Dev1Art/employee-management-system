package ru.dev1art.ems.config;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Dev1Art
 * @project EMS
 * @date 09.11.2024
 */

@SpringBootApplication
@ComponentScan(basePackages = "ru.dev1art.ems")
@EntityScan(basePackages = "ru.dev1art.ems.entities")
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
        Scene scene = new Scene(loader.load("/ru/dev1art/ems/EmployeeController.fxml"), 700, 400);
        scene.getStylesheets().add(EMS.class.getResource("/ru/dev1art/ems/style.css").toExternalForm());
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        configurableApplicationContext.close();
    }
}
