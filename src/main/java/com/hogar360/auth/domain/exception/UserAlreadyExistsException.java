package com.hogar360.auth.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private final String email;

    public UserAlreadyExistsException(String email) {
        super("Ya existe un usuario registrado con el correo: " + email);
        this.email = email;
    }

    public String getEmail() { return email; }
}
