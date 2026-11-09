package com.hogar360.statistics.domain.port.out;

import java.util.Optional;

public interface StatisticsHouseholdLookupPort {
    Optional<Long> findHouseholdIdByOwnerEmail(String ownerEmail);
}