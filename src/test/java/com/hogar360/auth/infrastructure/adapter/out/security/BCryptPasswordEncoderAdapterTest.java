package com.hogar360.auth.infrastructure.adapter.out.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();

    @Test
    void encode_noRetornaLaPasswordEnClaro() {
        String hash = adapter.encode("12345678");

        assertNotEquals("12345678", hash);
        assertTrue(hash.startsWith("$2"));
    }

    @Test
    void encode_mismaPasswordGeneraHashesDistintosPorElSalt() {
        assertNotEquals(adapter.encode("12345678"), adapter.encode("12345678"));
    }

    @Test
    void matches_conPasswordCorrecta_retornaTrue() {
        String hash = adapter.encode("12345678");

        assertTrue(adapter.matches("12345678", hash));
    }

    @Test
    void matches_conPasswordIncorrecta_retornaFalse() {
        String hash = adapter.encode("12345678");

        assertFalse(adapter.matches("otraPassword", hash));
    }
}