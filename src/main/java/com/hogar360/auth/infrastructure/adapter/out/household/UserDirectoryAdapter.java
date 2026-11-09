package com.hogar360.auth.infrastructure.adapter.out.household;

import com.hogar360.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.hogar360.household.domain.model.UserSummary;
import com.hogar360.household.domain.port.out.UserDirectoryPort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class UserDirectoryAdapter implements UserDirectoryPort {

    private final UserJpaRepository jpaRepository;

    public UserDirectoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<UserSummary> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findAllById(ids).stream()
                .map(u -> new UserSummary(u.getId(), u.getName(), u.getEmail()))
                .toList();
    }
}