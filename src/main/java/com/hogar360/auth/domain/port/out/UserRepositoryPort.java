package com.hogar360.auth.domain.port.out;

import com.hogar360.auth.domain.model.User;

import java.util.Optional;

/**
 * Puerto de salida: contrato que debe cumplir cualquier adaptador de persistencia
 * (JPA/MySQL, Mongo, memoria para tests, etc.) sin que el dominio lo sepa.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
