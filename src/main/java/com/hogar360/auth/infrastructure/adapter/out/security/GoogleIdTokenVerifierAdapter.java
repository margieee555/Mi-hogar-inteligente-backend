package com.hogar360.auth.infrastructure.adapter.out.security;

import com.hogar360.auth.domain.exception.InvalidGoogleTokenException;
import com.hogar360.auth.domain.model.GoogleIdentity;
import com.hogar360.auth.domain.port.out.GoogleIdentityPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Verifica el ID token de Google: firma (con las claves públicas de Google) y vencimiento
 * los comprueba el decoder; emisor, audiencia (nuestro client ID) y correo verificado, este adaptador.
 */
@Component
public class GoogleIdTokenVerifierAdapter implements GoogleIdentityPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleIdTokenVerifierAdapter.class);

    private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final Set<String> GOOGLE_ISSUERS = Set.of("https://accounts.google.com", "accounts.google.com");

    private final JwtDecoder decoder;
    private final String clientId;

    @Autowired
    public GoogleIdTokenVerifierAdapter(@Value("${google.client-id:}") String clientId) {
        this(NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWKS_URI).build(), clientId);
    }

    GoogleIdTokenVerifierAdapter(JwtDecoder decoder, String clientId) {
        this.decoder = decoder;
        this.clientId = clientId;
    }

    @Override
    public GoogleIdentity verify(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            log.warn("Google login rechazado: GOOGLE_CLIENT_ID no está configurado en el backend");
            throw new InvalidGoogleTokenException();
        }
        if (idToken == null || idToken.isBlank()) {
            log.warn("Google login rechazado: no llegó el idToken");
            throw new InvalidGoogleTokenException();
        }

        Jwt jwt;
        try {
            jwt = decoder.decode(idToken);
        } catch (JwtException e) {
            log.warn("Google login rechazado: no se pudo validar el token ({})", e.getMessage());
            throw new InvalidGoogleTokenException();
        }

        String issuer = jwt.getClaimAsString("iss");
        List<String> audience = jwt.getAudience();
        String email = jwt.getClaimAsString("email");
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

        if (!GOOGLE_ISSUERS.contains(issuer)) {
            log.warn("Google login rechazado: emisor inesperado '{}'", issuer);
            throw new InvalidGoogleTokenException();
        }
        if (audience == null || !audience.contains(clientId)) {
            log.warn("Google login rechazado: el token es para {} y el backend espera {}", audience, clientId);
            throw new InvalidGoogleTokenException();
        }
        if (email == null || email.isBlank() || !Boolean.TRUE.equals(emailVerified)) {
            log.warn("Google login rechazado: la cuenta no trae correo verificado");
            throw new InvalidGoogleTokenException();
        }

        return new GoogleIdentity(jwt.getSubject(), email.toLowerCase(), jwt.getClaimAsString("name"));
    }
}