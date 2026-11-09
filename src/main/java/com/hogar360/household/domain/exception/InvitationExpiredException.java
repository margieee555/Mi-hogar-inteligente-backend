package com.hogar360.household.domain.exception;

public class InvitationExpiredException extends RuntimeException {

    public InvitationExpiredException() {
        super("Esta invitación venció");
    }
}