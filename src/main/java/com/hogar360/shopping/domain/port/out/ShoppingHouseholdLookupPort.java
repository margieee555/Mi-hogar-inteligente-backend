package com.hogar360.shopping.domain.port.out;

import java.util.Optional;

/** Contrato hacia el módulo household: resuelve el hogar del usuario a partir del email del JWT. */
public interface ShoppingHouseholdLookupPort {
    Optional<Long> findHouseholdIdByOwnerEmail(String ownerEmail);
}