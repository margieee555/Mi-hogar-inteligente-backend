package com.hogar360.household.domain.exception;

public class HouseholdAccessDeniedException extends RuntimeException {

    public HouseholdAccessDeniedException() {
        super("No tienes permiso para modificar este hogar");
    }
}