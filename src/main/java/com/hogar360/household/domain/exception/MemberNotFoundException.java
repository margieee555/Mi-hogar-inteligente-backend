package com.hogar360.household.domain.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long userId) {
        super("No se encontró a ese miembro en tu hogar: " + userId);
    }
}