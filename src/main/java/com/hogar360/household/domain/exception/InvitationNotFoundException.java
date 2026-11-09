package com.hogar360.household.domain.exception;

public class InvitationNotFoundException extends RuntimeException {

    public InvitationNotFoundException(Long id) {
        super("No se encontró la invitación: " + id);
    }
}