package com.hogar360.auth.infrastructure.adapter.out.household;

import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserLookupAdapter implements UserLookupPort {

    private final UserRepositoryPort userRepositoryPort;

    public UserLookupAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Optional<Long> findUserIdByEmail(String email) {
        return userRepositoryPort.findByEmail(email).map(User::getId);
    }
}
