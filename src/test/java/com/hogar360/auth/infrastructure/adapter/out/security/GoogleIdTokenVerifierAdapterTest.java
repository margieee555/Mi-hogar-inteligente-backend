package com.hogar360.auth.infrastructure.adapter.out.security;

import com.hogar360.auth.domain.exception.InvalidGoogleTokenException;
import com.hogar360.auth.domain.model.GoogleIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GoogleIdTokenVerifierAdapterTest {

    private static final String CLIENT_ID = "client-123.apps.googleusercontent.com";

    private final JwtDecoder decoder = mock(JwtDecoder.class);
    private final GoogleIdTokenVerifierAdapter adapter = new GoogleIdTokenVerifierAdapter(decoder, CLIENT_ID);

    private Jwt.Builder validJwt() {
        return Jwt.withTokenValue("tok")
                .header("alg", "RS256")
                .subject("sub-1")
                .claim("iss", "https://accounts.google.com")
                .claim("aud", List.of(CLIENT_ID))
                .claim("email", "Ana@Mail.com")
                .claim("email_verified", true)
                .claim("name", "Ana");
    }

    @Test
    void verify_conTokenValido_devuelveLaIdentidadConCorreoEnMinusculas() {
        when(decoder.decode("tok")).thenReturn(validJwt().build());

        GoogleIdentity identity = adapter.verify("tok");

        assertThat(identity.subject()).isEqualTo("sub-1");
        assertThat(identity.email()).isEqualTo("ana@mail.com");
        assertThat(identity.name()).isEqualTo("Ana");
    }

    @Test
    void verify_aceptaElEmisorSinHttps() {
        when(decoder.decode("tok")).thenReturn(validJwt().claim("iss", "accounts.google.com").build());

        assertThat(adapter.verify("tok").email()).isEqualTo("ana@mail.com");
    }

    @Test
    void verify_conEmisorDistinto_falla() {
        when(decoder.decode("tok")).thenReturn(validJwt().claim("iss", "https://evil.example.com").build());

        assertThatThrownBy(() -> adapter.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void verify_conAudienciaDeOtraAplicacion_falla() {
        when(decoder.decode("tok")).thenReturn(validJwt().claim("aud", List.of("otra-app")).build());

        assertThatThrownBy(() -> adapter.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void verify_conCorreoNoVerificado_falla() {
        when(decoder.decode("tok")).thenReturn(validJwt().claim("email_verified", false).build());

        assertThatThrownBy(() -> adapter.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void verify_sinCorreo_falla() {
        when(decoder.decode("tok")).thenReturn(
                Jwt.withTokenValue("tok").header("alg", "RS256").subject("sub-1")
                        .claim("iss", "https://accounts.google.com")
                        .claim("aud", List.of(CLIENT_ID))
                        .claim("email_verified", true)
                        .build());

        assertThatThrownBy(() -> adapter.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void verify_siElDecoderRechazaElToken_falla() {
        when(decoder.decode("tok")).thenThrow(new BadJwtException("firma inválida"));

        assertThatThrownBy(() -> adapter.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
    }

    @Test
    void verify_sinTokenOSinClientId_fallaSinLlamarAlDecoder() {
        GoogleIdTokenVerifierAdapter sinClientId = new GoogleIdTokenVerifierAdapter(decoder, "");

        assertThatThrownBy(() -> adapter.verify(" ")).isInstanceOf(InvalidGoogleTokenException.class);
        assertThatThrownBy(() -> adapter.verify(null)).isInstanceOf(InvalidGoogleTokenException.class);
        assertThatThrownBy(() -> sinClientId.verify("tok")).isInstanceOf(InvalidGoogleTokenException.class);
        verifyNoInteractions(decoder);
    }
}