package com.hogar360.shopping.application.service;

import com.hogar360.shopping.domain.exception.ShoppingHouseholdRequiredException;
import com.hogar360.shopping.domain.port.out.ShoppingHouseholdLookupPort;
import org.springframework.stereotype.Component;

/** Regla única reutilizada por todos los casos de uso: el usuario debe tener un hogar configurado. */
@Component
public class ShoppingHouseholdResolver {

    private final ShoppingHouseholdLookupPort householdLookupPort;

    public ShoppingHouseholdResolver(ShoppingHouseholdLookupPort householdLookupPort) {
        this.householdLookupPort = householdLookupPort;
    }

    public Long requireHouseholdId(String ownerEmail) {
        return householdLookupPort.findHouseholdIdByOwnerEmail(ownerEmail)
                .orElseThrow(ShoppingHouseholdRequiredException::new);
    }
}
