package com.hogar360.household.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvitationTest {

    @Test
    void createNew_normalizaEmailYQuedaPendiente() {
        Invitation inv = Invitation.createNew(7L, "  Ana@Mail.COM ");

        assertNull(inv.getId());
        assertEquals(7L, inv.getHouseholdId());
        assertEquals("ana@mail.com", inv.getEmail());
        assertEquals(Invitation.Status.PENDING, inv.getStatus());
    }

    @Test
    void createNew_conEmailNulo_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Invitation.createNew(7L, null));
    }

    @Test
    void createNew_conEmailEnBlanco_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Invitation.createNew(7L, "   "));
    }
}