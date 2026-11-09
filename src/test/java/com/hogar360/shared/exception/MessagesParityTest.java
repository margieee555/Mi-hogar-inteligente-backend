package com.hogar360.shared.exception;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesParityTest {

    private Properties load(String resource) throws Exception {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertThat(in).as("no existe " + resource).isNotNull();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        return props;
    }

    @Test
    void español_e_ingles_tienenLasMismasClaves() throws Exception {
        Properties es = load("messages.properties");
        Properties en = load("messages_en.properties");

        assertThat(new TreeSet<>(en.stringPropertyNames()))
                .isEqualTo(new TreeSet<>(es.stringPropertyNames()));
    }

    @Test
    void ningunMensajeEstaVacio() throws Exception {
        for (String file : new String[]{"messages.properties", "messages_en.properties"}) {
            Properties props = load(file);
            props.forEach((key, value) ->
                    assertThat(value.toString().trim()).as(file + " -> " + key).isNotEmpty());
        }
    }
}