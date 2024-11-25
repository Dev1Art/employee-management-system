package ru.dev1art.ems.util.lang;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Utility class for internationalization (I18N) functionalities.
 * This class provides methods to manage locale settings,
 * localize strings based on keys, and create bindings for localized strings.
 *
 * @author Dev1Art
 * @project EMS
 * @date 11.11.2024
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class I18NUtil {
    static final ObjectProperty<Locale> locale;
    static final Marker I18N_MARKER = MarkerFactory.getMarker("I18N");

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> {
            Locale.setDefault(newValue);
            log.debug(I18N_MARKER, "Locale changed to: {}", newValue);
        });
    }

    /**
     * Retrieves the default locale for the application.
     * The default locale is set to the system's default if it is supported,
     * otherwise it defaults to English (GB).
     *
     * @return The default Locale.
     */
    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        Locale defaultLocale = getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
        log.debug(I18N_MARKER, "Default locale set to: {}", defaultLocale);
        return defaultLocale;
    }

    /**
     * Gets the current locale being used by the application.
     *
     * @return The current Locale.
     */
    public static Locale getLocale() {
        return locale.get();
    }

    /**
     * Returns the property representing the current locale.
     * This can be used to bind UI elements to the locale property.
     *
     * @return An ObjectProperty representing the current Locale.
     */
    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * Sets the current locale for the application.
     * This updates the locale property and sets the default locale accordingly.
     *
     * @param locale The new Locale to set.
     */
    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    /**
     * Retrieves a list of supported locales for the application.
     *
     * @return A List of supported Locale objects.
     */
    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(
                new Locale.Builder().setLanguage("en").setRegion("GB").build(),
                new Locale.Builder().setLanguage("ru").setScript("Cyrl").build()
        ));
    }

    /**
     * Creates a StringBinding for a localized string based on a key and optional arguments.
     *
     * @param key The key for the localized string.
     * @param args Optional arguments to format the localized string.
     * @return A StringBinding that provides the localized string.
     */
    public static StringBinding createStringBinding(final String key, Object... args) {
        log.trace(I18N_MARKER, "Creating string binding for key: {}", key);
        return Bindings.createStringBinding(() -> localize(key, args), locale);
    }

    /**
     * Creates a StringBinding using a Callable that returns a localized string.
     *
     * @param func A Callable that returns a localized string.
     * @return A StringBinding that provides the localized string.
     */
    public static StringBinding createStringBinding(Callable<String> func) {
        log.trace(I18N_MARKER, "Creating string binding for key: {}", func.toString());
        return Bindings.createStringBinding(func, locale);
    }

    /**
     * Localizes a string based on the provided key and optional arguments.
     *
     * @param key The key for the localized string.
     * @param args Optional arguments to format the localized string.
     * @return The localized string, or the key if the resource is missing.
     */
    public static String localize(final String key, final Object... args) {
        log.trace(I18N_MARKER, "Localizing key: {}", key);

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("ru.dev1art.ems.lang.text", getLocale());
            String localizedString = MessageFormat.format(resourceBundle.getString(key), args);
            log.trace(I18N_MARKER, "Localized string: {}", localizedString);
            return localizedString;
        } catch (MissingResourceException exception) {
            log.error("Missing resource for key: {}. Locale: {}", key, getLocale(), exception);
            return key;
        }
    }
}
