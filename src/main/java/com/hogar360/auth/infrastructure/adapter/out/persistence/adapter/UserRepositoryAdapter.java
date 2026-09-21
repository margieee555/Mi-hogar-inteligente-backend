package com.hogar360.auth.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import com.hogar360.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.hogar360.auth.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.hogar360.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementa el puerto de salida UserRepositoryPort usando JPA/MySQL.
 * Es el único punto donde el dominio "toca" la base de datos, indirectamente.
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository, UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity saved = jpaRepository.save(mapper.toEntity(user));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
