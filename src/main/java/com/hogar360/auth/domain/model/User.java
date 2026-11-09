package com.hogar360.auth.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio pura. No conoce JPA, ni Spring, ni ningún framework.
 * Representa las reglas de negocio del usuario dentro de Hogar360.
 */
public class User {

    private final Long id;
    private final String name;
    private final String email;
    private final String password; // ya viene encriptada cuando llega aquí
    private final Role role;
    private final LocalDateTime createdAt;
    private final AuthProvider authProvider;

    public User(Long id, String name, String email, String password, Role role, LocalDateTime createdAt) {
        this(id, name, email, password, role, createdAt, AuthProvider.LOCAL);
    }

    public User(Long id, String name, String email, String password, Role role,
                LocalDateTime createdAt, AuthProvider authProvider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.authProvider = authProvider;
    }

    /** Fábrica para un usuario nuevo (aún sin id, lo asigna la persistencia). */
    public static User createNew(String name, String email, String encodedPassword) {
        return new User(null, name, email, encodedPassword, Role.USER, LocalDateTime.now(), AuthProvider.LOCAL);
    }

    /** Usuario creado desde Google: su contraseña es un hash de un valor aleatorio que nadie conoce. */
    public static User createFromGoogle(String name, String email, String unusablePasswordHash) {
        return new User(null, name, email, unusablePasswordHash, Role.USER, LocalDateTime.now(), AuthProvider.GOOGLE);
    }

    /** Pasa una cuenta con contraseña a cuenta de Google, anulando la contraseña anterior. */
    public User linkedToGoogle(String unusablePasswordHash) {
        return new User(id, name, email, unusablePasswordHash, role, createdAt, AuthProvider.GOOGLE);
    }

        /** Contraseña nueva (ya encriptada). Una cuenta de Google que la define también puede entrar con correo y contraseña. */
    public User withPassword(String encodedPassword) {
        return new User(id, name, email, encodedPassword, role, createdAt, authProvider);
    }

    public boolean isGoogleAccount() { return authProvider == AuthProvider.GOOGLE; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AuthProvider getAuthProvider() { return authProvider; }
}