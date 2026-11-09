package com.hogar360.task.domain.port.out;

import java.util.Map;

/** Contrato hacia el módulo household: miembros del hogar (id de usuario -> nombre). */
public interface TaskAssigneeLookupPort {
    Map<Long, String> memberNames(Long householdId, String requesterEmail);
}