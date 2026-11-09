package com.hogar360.finance.application.service;

import com.hogar360.finance.domain.exception.HouseholdRequiredException;
import com.hogar360.finance.domain.port.out.HouseholdLookupPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FinanceHouseholdResolverTest {

    private final HouseholdLookupPort lookup = mock(HouseholdLookupPort.class);
    private final FinanceHouseholdResolver resolver = new FinanceHouseholdResolver(lookup);

    @Test
    void devuelveElIdDelHogarDelUsuario() {
        when(lookup.findHouseholdIdByOwnerEmail("ana@x.com")).thenReturn(Optional.of(5L));

        assertThat(resolver.requireHouseholdId("ana@x.com")).isEqualTo(5L);
    }

    @Test
    void lanzaHouseholdRequiredSiElUsuarioNoTieneHogar() {
        when(lookup.findHouseholdIdByOwnerEmail("ana@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.requireHouseholdId("ana@x.com"))
                .isInstanceOf(HouseholdRequiredException.class);
    }
}