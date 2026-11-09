package com.hogar360.auth.domain.port.in;

import com.hogar360.auth.application.dto.RefreshResult;
import com.hogar360.auth.domain.model.User;

public interface SessionUseCase {
    /** Abre una sesión nueva y devuelve el refresh token en crudo (para la cookie). */
    String openSession(User user);

    /** Cambia un refresh token válido por otro nuevo y emite un access token. */
    RefreshResult refresh(String rawRefreshToken);

    /** Cierra la sesión; no falla si el token no existe. */
    void closeSession(String rawRefreshToken);
}
