package com.hogar360.auth.domain.port.out;

import com.hogar360.auth.domain.model.GoogleIdentity;

public interface GoogleIdentityPort {
    /** Valida el ID token de Google y devuelve la identidad; lanza InvalidGoogleTokenException si no es válido. */
    GoogleIdentity verify(String idToken);
}