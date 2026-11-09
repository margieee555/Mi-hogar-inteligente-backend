package com.hogar360.household.domain.model;

import com.hogar360.household.domain.exception.InvitationNotPendingException;
import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationLifecycleTest {

    @Test
    void crearNormalizaElCorreoYVenceEnSieteDias() {
        Invitation invitation = Invitation.createNew(10L, "  Luis@Mail.COM ");

        assertEquals("luis@mail.com", invitation.getEmail());
        assertEquals(Invitation.Status.PENDING, invitation.getStatus());
        assertFalse(invitation.isExpired(LocalDateTime.now()));
        assertTrue(invitation.isExpired(LocalDateTime.now().plusDays(8)));
    }

    @Test
    void sinCorreoLanzaErrorDeValidacion() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> Invitation.createNew(10L, "  "));

        assertEquals("error.household.invitation.emailRequired", ex.getCode());
    }

    @Test
    void unaInvitacionPendienteSePuedeAceptarRechazarOCancelar() {
        Invitation invitation = Invitation.createNew(10L, "luis@mail.com");

        assertEquals(Invitation.Status.ACCEPTED, invitation.accept().getStatus());
        assertEquals(Invitation.Status.DECLINED, invitation.decline().getStatus());
        assertEquals(Invitation.Status.CANCELLED, invitation.cancel().getStatus());
    }

    @Test
    void unaInvitacionYaRespondidaNoSePuedeRespondeDeNuevo() {
        Invitation aceptada = Invitation.createNew(10L, "luis@mail.com").accept();

        assertThrows(InvitationNotPendingException.class, aceptada::decline);
        assertThrows(InvitationNotPendingException.class, aceptada::cancel);
    }
}