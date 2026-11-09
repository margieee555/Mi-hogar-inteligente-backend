package com.hogar360.finance.domain.exception;

public class HouseholdRequiredException extends RuntimeException {

    public HouseholdRequiredException() {
        super("Debes configurar tu hogar antes de usar las finanzas");
    }
}