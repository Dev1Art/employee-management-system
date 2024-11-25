package ru.dev1art.ems.config;

import javafx.fxml.FXMLLoader;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpringFXMLLoaderTest {
    @Mock ApplicationContext applicationContext;
    @InjectMocks SpringFXMLLoader springFXMLLoader;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadFXMLSuccessfully() throws IOException {
        URL mockUrl = getClass().getResource("/ru/dev1art/ems/MainController.fxml");
        assertNotNull(mockUrl, "FXML resource should exist");

        FXMLLoader loader = mock(FXMLLoader.class);
        when(loader.load()).thenReturn(new Object());
        when(applicationContext.getBean(FXMLLoader.class)).thenReturn(loader);

        doNothing().when(loader).setControllerFactory(any());

        Object result = springFXMLLoader.load("/ru/dev1art/ems/MainController.fxml");

        assertNotNull(result, "Loaded object should not be null");
        verify(loader).setControllerFactory(applicationContext::getBean);
        verify(loader).load();
    }


    @Test
    public void testLoadFXMLResourceNotFound() {
        String fxmlPath = "/invalid/path/NotFound.fxml";

        IOException exception = assertThrows(IOException.class, () -> {
            springFXMLLoader.load(fxmlPath);
        });

        assertEquals("FXML resource not found: " + fxmlPath, exception.getMessage());
    }

    @Test
    public void testLoadFXMLWithIOException() throws Exception {
        URL mockUrl = getClass().getResource("/ru/dev1art/ems/MainController.fxml");
        assertNotNull(mockUrl, "FXML resource should exist");

        FXMLLoader loader = mock(FXMLLoader.class);
        when(loader.load()).thenThrow(new IOException("Mocked IOException"));
        when(applicationContext.getBean(FXMLLoader.class)).thenReturn(loader);

        doNothing().when(loader).setControllerFactory(any());

        IOException exception = assertThrows(IOException.class, () -> {
            springFXMLLoader.load("/ru/dev1art/ems/MainController.fxml");
        });

        assertEquals("Mocked IOException", exception.getMessage());
        verify(loader).setControllerFactory(applicationContext::getBean);
        verify(loader).load();
    }

}