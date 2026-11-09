package com.hogar360.household.domain.exception;

public class InvitationNotPendingException extends RuntimeException {

    public InvitationNotPendingException() {
        super("Esta invitación ya fue respondida o cancelada");
    }
}