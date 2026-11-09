package com.hogar360.household.infrastructure.adapter.out.task;

import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.model.MemberView;
import com.hogar360.household.domain.port.in.ManageMembershipUseCase;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskAssigneeLookupAdapterTest {

    private final ManageMembershipUseCase membership = mock(ManageMembershipUseCase.class);
    private final TaskAssigneeLookupAdapter adapter = new TaskAssigneeLookupAdapter(membership);

    private static MemberView member(Long userId, String name, MemberRole role) {
        return new MemberView(userId, name, name.toLowerCase() + "@x.com", role, LocalDateTime.now());
    }

    @Test
    void devuelveIdYNombreDeLosMiembrosDelHogar() {
        when(membership.listMembers(5L, "ana@x.com"))
                .thenReturn(List.of(member(1L, "Ana", MemberRole.OWNER), member(2L, "Luis", MemberRole.MEMBER)));

        Map<Long, String> names = adapter.memberNames(5L, "ana@x.com");

        assertThat(names).containsEntry(1L, "Ana").containsEntry(2L, "Luis").hasSize(2);
    }

    @Test
    void sinMiembrosDevuelveUnMapaVacio() {
        when(membership.listMembers(5L, "ana@x.com")).thenReturn(List.of());

        assertThat(adapter.memberNames(5L, "ana@x.com")).isEmpty();
    }
}