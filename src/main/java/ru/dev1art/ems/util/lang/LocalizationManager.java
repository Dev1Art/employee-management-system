package ru.dev1art.ems.util.lang;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Singleton class responsible for managing localization settings in the application.
 * It allows setting the current locale, notifying listeners of locale changes,
 * and providing access to the current locale.
 *
 * @author Dev1Art
 * @project EMS
 * @date 24.11.2024
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalizationManager {
    static final Marker I18N_MARKER = MarkerFactory.getMarker("I18N");
    static LocalizationManager instance;
    @Getter Locale currentLocale;
    @Getter final List<LocaleChangeListener> listeners = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the LocalizationManager with the default locale.
     */
    private LocalizationManager() {
        currentLocale = Locale.getDefault();
        log.debug(I18N_MARKER, "LocalizationManager initialized with default locale: {}", currentLocale);
    }

    /**
     * Retrieves the singleton instance of the LocalizationManager.
     * This method is synchronized to ensure thread-safe access.
     *
     * @return The singleton instance of LocalizationManager.
     */
    public static synchronized LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    /**
     * Sets the current locale for the application.
     * Notifies all registered listeners about the locale change.
     *
     * @param locale The new Locale to set.
     */
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        I18NUtil.setLocale(locale);
        log.info(I18N_MARKER, "Locale set to: {}", locale);
        notifyLocaleChange(locale);
    }

    /**
     * Adds a listener that will be notified when the locale changes.
     *
     * @param listener The LocaleChangeListener to add.
     */
    public void addLocaleChangeListener(LocaleChangeListener listener) {
        listeners.add(listener);
        log.debug(I18N_MARKER, "LocaleChangeListener added: {}", listener.getClass().getName());
    }

    /**
     * Notifies all registered listeners of a change in the locale.
     *
     * @param newLocale The new Locale that has been set.
     */
    private void notifyLocaleChange(Locale newLocale) {
        log.debug(I18N_MARKER, "Notifying {} listeners of locale change to {}", listeners.size(), newLocale);
        for (LocaleChangeListener listener : listeners) {
            listener.localeChanged(newLocale);
        }
    }
}