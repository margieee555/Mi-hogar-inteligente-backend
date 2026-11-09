package com.hogar360.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class I18nConfig {

    /** El nombre del bean debe ser exactamente "localeResolver". */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        Locale spanish = Locale.forLanguageTag("es");
        resolver.setSupportedLocales(List.of(spanish, Locale.ENGLISH));
        resolver.setDefaultLocale(spanish);
        return resolver;
    }
}
