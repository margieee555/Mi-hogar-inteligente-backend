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

    public User(Long id, String name, String email, String password, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    /** Fábrica para un usuario nuevo (aún sin id, lo asigna la persistencia). */
    public static User createNew(String name, String email, String encodedPassword) {
        return new User(null, name, email, encodedPassword, Role.USER, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
