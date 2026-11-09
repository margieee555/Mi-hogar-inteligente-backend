package com.hogar360.household.domain.exception;

public class HouseholdNotFoundException extends RuntimeException {

    public HouseholdNotFoundException(Long id) {
        super("No se encontró un hogar con id: " + id);
    }

    public HouseholdNotFoundException(String message) {
        super(message);
    }
}
