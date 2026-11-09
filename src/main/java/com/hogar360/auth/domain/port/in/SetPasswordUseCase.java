package com.hogar360.auth.domain.port.in;

public interface SetPasswordUseCase {
    /** Solo cuentas de Google: define una contraseña para poder entrar también con correo y contraseña. */
    void setPassword(String email, String newPassword, String confirmPassword);
}
