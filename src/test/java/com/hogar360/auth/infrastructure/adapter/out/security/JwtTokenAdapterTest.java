package com.hogar360.auth.infrastructure.adapter.out.security;

import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenAdapterTest {

    // HS256 exige una clave de al menos 32 bytes
    private static final String SECRET = "clave-secreta-de-prueba-con-32-bytes-minimo!!";
    private static final long ONE_HOUR = 3_600_000L;

    private final JwtTokenAdapter adapter = new JwtTokenAdapter(SECRET, ONE_HOUR);

    private User buildUser(Role role) {
        return new User(1L, "Juan", "juan@mail.com", "hash", role, LocalDateTime.now());
    }

    @Test
    void generateToken_generaTokenValidoConEmailYRol() {
        String token = adapter.generateToken(buildUser(Role.ADMIN));

        assertNotNull(token);
        assertTrue(adapter.isValid(token));
        assertEquals("juan@mail.com", adapter.extractEmail(token));
        assertEquals("ADMIN", adapter.extractRole(token));
    }

    @Test
    void isValid_conTokenExpirado_retornaFalse() {
        JwtTokenAdapter expiredAdapter = new JwtTokenAdapter(SECRET, -1000L);

        String token = expiredAdapter.generateToken(buildUser(Role.USER));

        assertFalse(expiredAdapter.isValid(token));
    }

    @Test
    void isValid_conTokenFirmadoConOtraClave_retornaFalse() {
        JwtTokenAdapter otherAdapter =
                new JwtTokenAdapter("otra-clave-distinta-de-prueba-con-32-bytes!!", ONE_HOUR);
        String foreignToken = otherAdapter.generateToken(buildUser(Role.USER));

        assertFalse(adapter.isValid(foreignToken));
    }

    @Test
    void isValid_conTokenManipulado_retornaFalse() {
        String token = adapter.generateToken(buildUser(Role.USER));
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertFalse(adapter.isValid(tampered));
    }

    @Test
    void isValid_conTextoBasura_retornaFalse() {
        assertFalse(adapter.isValid("esto-no-es-un-jwt"));
    }
}
