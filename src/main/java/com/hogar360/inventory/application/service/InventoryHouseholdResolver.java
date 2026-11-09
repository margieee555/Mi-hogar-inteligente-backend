package com.hogar360.inventory.application.service;

import com.hogar360.inventory.domain.exception.InventoryHouseholdRequiredException;
import com.hogar360.inventory.domain.port.out.InventoryHouseholdLookupPort;
import org.springframework.stereotype.Component;

/** Regla única reutilizada por todos los casos de uso: el usuario debe tener un hogar configurado. */
@Component
public class InventoryHouseholdResolver {

    private final InventoryHouseholdLookupPort householdLookupPort;

    public InventoryHouseholdResolver(InventoryHouseholdLookupPort householdLookupPort) {
        this.householdLookupPort = householdLookupPort;
    }

    public Long requireHouseholdId(String ownerEmail) {
        return householdLookupPort.findHouseholdIdByOwnerEmail(ownerEmail)
                .orElseThrow(InventoryHouseholdRequiredException::new);
    }
}