package ru.dev1art.ems.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author Dev1Art
 * @project EMS
 * @date 11.11.2024
 */

public class I18NUtil {
    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(
                new Locale.Builder().setLanguage("en").setRegion("GB").build(),
                new Locale.Builder().setLanguage("ru").setScript("Cyrl").build()
        ));
    }

    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> localize(key, args), locale);
    }

    public static StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, locale);
    }

    public static String localize(final String key, final Object... args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("text", getLocale());
        return MessageFormat.format(resourceBundle.getString(key), args);
    }
}
