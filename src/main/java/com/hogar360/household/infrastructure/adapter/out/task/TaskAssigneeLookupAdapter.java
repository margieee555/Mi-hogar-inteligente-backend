package com.hogar360.household.infrastructure.adapter.out.task;

import com.hogar360.household.domain.model.MemberView;
import com.hogar360.household.domain.port.in.ManageMembershipUseCase;
import com.hogar360.task.domain.port.out.TaskAssigneeLookupPort;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TaskAssigneeLookupAdapter implements TaskAssigneeLookupPort {

    private final ManageMembershipUseCase membershipUseCase;

    public TaskAssigneeLookupAdapter(ManageMembershipUseCase membershipUseCase) {
        this.membershipUseCase = membershipUseCase;
    }

    @Override
    public Map<Long, String> memberNames(Long householdId, String requesterEmail) {
        Map<Long, String> names = new LinkedHashMap<>();
        for (MemberView member : membershipUseCase.listMembers(householdId, requesterEmail)) {
            names.put(member.userId(), member.name());
        }
        return names;
    }
}
