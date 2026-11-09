package com.hogar360.household.domain.port.in;

/** Lo consume el módulo auth en el login para saber si el usuario ya terminó el onboarding. */
public interface HasHouseholdUseCase {
    boolean hasCompletedOnboarding(Long userId);
}