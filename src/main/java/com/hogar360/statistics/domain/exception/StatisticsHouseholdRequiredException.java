package com.hogar360.statistics.domain.exception;

import com.hogar360.shared.exception.DomainValidationException;

/** Reutiliza el código ya traducido: el usuario debe configurar su hogar primero. */
public class StatisticsHouseholdRequiredException extends DomainValidationException {

    public StatisticsHouseholdRequiredException() {
        super("error.household.required", "Debes configurar tu hogar antes de usar esta función");
    }
}