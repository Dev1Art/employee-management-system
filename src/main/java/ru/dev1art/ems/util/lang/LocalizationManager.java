package ru.dev1art.ems.util.lang;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dev1Art
 * @project EMS
 * @date 24.11.2024
 */

public class LocalizationManager {
    private static LocalizationManager instance;
    @Getter
    private Locale currentLocale;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();

    private LocalizationManager() {
        currentLocale = Locale.getDefault();
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        I18NUtil.setLocale(locale);
        notifyLocaleChange(locale);
    }

    public void addLocaleChangeListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyLocaleChange(Locale newLocale) {
        for (LocaleChangeListener listener : listeners) {
            listener.localeChanged(newLocale);
        }
    }
}
