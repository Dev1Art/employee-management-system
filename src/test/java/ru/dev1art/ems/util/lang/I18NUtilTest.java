package ru.dev1art.ems.util.lang;

import javafx.beans.binding.StringBinding;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dev1Art
 * @project EMS
 * @date 25.11.2024
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public class I18NUtilTest {

    static final String TEST_KEY = "greeting";
    static final String TEST_LOCALIZED_STRING = "Hello, World!";

    @BeforeEach
    public void setUp() {
        I18NUtil.setLocale(Locale.ENGLISH);

        ResourceBundle resourceBundle = mock(ResourceBundle.class);
        when(resourceBundle.getString(TEST_KEY)).thenReturn(TEST_LOCALIZED_STRING);
        ResourceBundle.clearCache();
        ResourceBundle.getBundle("ru.dev1art.ems.lang.text-test");
    }

    @Test
    public void testGetDefaultLocale() {
        Locale defaultLocale = I18NUtil.getDefaultLocale();
        assertEquals(Locale.ENGLISH, defaultLocale);
    }

    @Test
    public void testGetSupportedLocales() {
        var supportedLocales = I18NUtil.getSupportedLocales();
        assertTrue(supportedLocales.contains(Locale.ENGLISH));
        assertTrue(supportedLocales.contains(new Locale("ru", "RU")));
    }

    @Test
    public void testSetLocale() {
        I18NUtil.setLocale(new Locale("ru", "RU"));
        assertEquals(new Locale("ru", "RU"), I18NUtil.getLocale());
    }

    @Test
    public void testLocalize_ValidKey() {
        String localizedString = I18NUtil.localize(TEST_KEY);
        assertEquals(TEST_LOCALIZED_STRING, localizedString);
    }

    @Test
    public void testLocalize_InvalidKey() {
        String invalidKey = "invalid.key";
        String localizedString = I18NUtil.localize(invalidKey);
        assertEquals(invalidKey, localizedString);
    }

    @Test
    public void testCreateStringBinding() {
        StringBinding binding = I18NUtil.createStringBinding(TEST_KEY);
        assertNotNull(binding);
        assertEquals(TEST_LOCALIZED_STRING, binding.get());
    }

    @Test
    public void testCreateStringBinding_WithArgs() {
        StringBinding binding = I18NUtil.createStringBinding(TEST_KEY, "John");
        assertNotNull(binding);
        assertEquals("Hello, Doe!", binding.get());
    }

    @Test
    public void testCreateStringBinding_WithCallable() {
        StringBinding binding = I18NUtil.createStringBinding(() -> TEST_LOCALIZED_STRING);
        assertNotNull(binding);
        assertEquals(TEST_LOCALIZED_STRING, binding.get());
    }
}