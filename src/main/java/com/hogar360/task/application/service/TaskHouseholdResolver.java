package com.hogar360.task.application.service;

import com.hogar360.task.domain.exception.TaskHouseholdRequiredException;
import com.hogar360.task.domain.port.out.TaskHouseholdLookupPort;
import org.springframework.stereotype.Component;

/** Regla única reutilizada por todos los casos de uso: el usuario debe tener un hogar configurado. */
@Component
public class TaskHouseholdResolver {

    private final TaskHouseholdLookupPort householdLookupPort;

    public TaskHouseholdResolver(TaskHouseholdLookupPort householdLookupPort) {
        this.householdLookupPort = householdLookupPort;
    }

    public Long requireHouseholdId(String ownerEmail) {
        return householdLookupPort.findHouseholdIdByOwnerEmail(ownerEmail)
                .orElseThrow(TaskHouseholdRequiredException::new);
    }
}