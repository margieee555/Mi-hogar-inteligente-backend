package com.hogar360.inventory.domain.port.out;

import java.util.Optional;

/** Contrato hacia el módulo household: resuelve el hogar del usuario a partir del email del JWT. */
public interface InventoryHouseholdLookupPort {
    Optional<Long> findHouseholdIdByOwnerEmail(String ownerEmail);
}
