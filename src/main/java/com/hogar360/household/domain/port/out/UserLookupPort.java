package com.hogar360.household.domain.port.out;

import java.util.Optional;

/** Contrato hacia el módulo auth: resuelve el id de usuario a partir del email del JWT. */
public interface UserLookupPort {
    Optional<Long> findUserIdByEmail(String email);
}