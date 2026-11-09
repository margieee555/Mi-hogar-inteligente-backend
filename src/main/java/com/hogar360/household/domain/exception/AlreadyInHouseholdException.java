package com.hogar360.household.domain.exception;

public class AlreadyInHouseholdException extends RuntimeException {

    public AlreadyInHouseholdException() {
        super("Ya perteneces a un hogar. Sal de él antes de aceptar otra invitación");
    }
}