package com.hogar360.shared.exception;

/**
 * Error de validación de reglas de negocio con un código estable para traducirlo.
 * Sigue siendo una IllegalArgumentException; el mensaje original es el texto de respaldo.
 */
public class DomainValidationException extends IllegalArgumentException {

    private final String code;
    private final transient Object[] args;

    public DomainValidationException(String code, String defaultMessage, Object... args) {
        super(defaultMessage);
        this.code = code;
        this.args = args;
    }

    public String getCode() { return code; }
    public Object[] getArgs() { return args; }
}