package com.mono.mysettings.settingInputAndLanguage.Bean;

import java.util.Locale;

public class CurrentLocale {
    String name;
    Locale locale;

    public CurrentLocale(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "CurrentLocale{" +
                "name='" + name + '\'' +
                ", locale=" + locale +
                '}';
    }
}
