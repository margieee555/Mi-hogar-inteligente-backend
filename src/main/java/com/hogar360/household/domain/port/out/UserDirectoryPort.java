package com.hogar360.household.domain.port.out;

import com.hogar360.household.domain.model.UserSummary;

import java.util.Collection;
import java.util.List;

/** Contrato hacia el módulo auth: nombre y correo de varios usuarios a partir de sus ids. */
public interface UserDirectoryPort {
    List<UserSummary> findByIds(Collection<Long> ids);
}