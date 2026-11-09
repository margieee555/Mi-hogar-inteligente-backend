package com.hogar360.finance.application.service;

import com.hogar360.finance.domain.exception.HouseholdRequiredException;
import com.hogar360.finance.domain.port.out.HouseholdLookupPort;
import org.springframework.stereotype.Component;

/** Regla única reutilizada por todos los casos de uso: el usuario debe tener un hogar configurado. */
@Component
public class FinanceHouseholdResolver {

    private final HouseholdLookupPort householdLookupPort;

    public FinanceHouseholdResolver(HouseholdLookupPort householdLookupPort) {
        this.householdLookupPort = householdLookupPort;
    }

    public Long requireHouseholdId(String ownerEmail) {
        return householdLookupPort.findHouseholdIdByOwnerEmail(ownerEmail)
                .orElseThrow(HouseholdRequiredException::new);
    }
}