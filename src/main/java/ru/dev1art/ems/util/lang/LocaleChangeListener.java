package ru.dev1art.ems.util.lang;

import java.util.Locale;

/**
 * Interface for listening to changes in the application's locale.
 * Implementing classes can use this interface to respond to locale changes
 * and update their behavior or display accordingly.
 *
 * @author Dev1Art
 * @project EMS
 * @date 24.11.2024
 */

public interface LocaleChangeListener {
    /**
     * Called when the locale has changed.
     *
     * @param newLocale The new Locale that has been set.
     */
    void localeChanged(Locale newLocale);
}
