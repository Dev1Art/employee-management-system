package ru.dev1art.ems.util.lang;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalizationManagerTest {
    LocalizationManager localizationManager;

    @BeforeEach
    public void setUp() {
        localizationManager = LocalizationManager.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        LocalizationManager anotherInstance = LocalizationManager.getInstance();
        assertSame(localizationManager, anotherInstance, "Instances should be the same");
    }

    @Test
    public void testSetLocale() {
        Locale newLocale = new Locale("fr", "FR");
        localizationManager.setLocale(newLocale);
        assertEquals(newLocale, localizationManager.getCurrentLocale(), "Current locale should be updated");
    }

    @Test
    public void testAddLocaleChangeListener() {
        LocaleChangeListener listener = Mockito.mock(LocaleChangeListener.class);
        localizationManager.addLocaleChangeListener(listener);

        assertEquals(1, localizationManager.getListeners().size(), "Listener should be added");
    }

    @Test
    public void testNotifyLocaleChange() {
        LocaleChangeListener listener = Mockito.mock(LocaleChangeListener.class);
        localizationManager.addLocaleChangeListener(listener);

        Locale newLocale = new Locale("de", "DE");
        localizationManager.setLocale(newLocale);

        verify(listener).localeChanged(newLocale);
    }

    @Test
    public void testNotifyLocaleChange_NoListeners() {
        Locale newLocale = new Locale("es", "ES");
        localizationManager.setLocale(newLocale);

        assertEquals(newLocale, localizationManager.getCurrentLocale(), "Current locale should be updated");
    }
}
