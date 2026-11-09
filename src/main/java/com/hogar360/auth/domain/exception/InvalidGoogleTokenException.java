package com.hogar360.auth.domain.exception;

public class InvalidGoogleTokenException extends RuntimeException {
    public InvalidGoogleTokenException() {
        super("No pudimos verificar tu cuenta de Google");
    }
}