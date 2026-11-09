package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.UpdateHouseholdCommand;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.HouseholdMember;
import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateHouseholdServiceTest {

    private static final String OWNER = "ana@mail.com";

    @Mock private HouseholdRepositoryPort householdRepository;
    @Mock private HouseholdOwnershipChecker ownershipChecker;
    @Mock private HouseholdMemberRepositoryPort memberRepository;
    @Mock private InvitationRepositoryPort invitationRepository;

    private UpdateHouseholdService service;
    private Household hogar;

    @BeforeEach
    void setUp() {
        service = new UpdateHouseholdService(householdRepository, ownershipChecker, memberRepository, invitationRepository);
        hogar = new Household(10L, 1L, "Casa", HomeType.CASA, 3, true, List.of(), LocalDateTime.now());
        when(ownershipChecker.requireOwned(10L, OWNER)).thenReturn(hogar);
        when(householdRepository.save(any(Household.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime ahora = LocalDateTime.now();
        when(memberRepository.findByHouseholdId(10L)).thenReturn(List.of(
                new HouseholdMember(100L, 10L, 1L, MemberRole.OWNER, ahora),
                new HouseholdMember(101L, 10L, 7L, MemberRole.MEMBER, ahora)));
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of(
                new Invitation(5L, 10L, "pedro@mail.com", Invitation.Status.PENDING, ahora, ahora.plusDays(3)),
                new Invitation(6L, 10L, "vencida@mail.com", Invitation.Status.PENDING, ahora.minusDays(9), ahora.minusDays(2))));
    }

    @Test
    void elDuenoActualizaLosDatosDelHogar() {
        Household actualizado = service.update(
                new UpdateHouseholdCommand(10L, OWNER, "Mi apto", HomeType.APARTAMENTO, 4));

        assertEquals("Mi apto", actualizado.getName());
        assertEquals(HomeType.APARTAMENTO, actualizado.getType());
        assertEquals(4, actualizado.getResidentCount());
    }

    @Test
    void noPuedeBajarLosResidentesPorDebajoDeLosCuposOcupados() {
        // 2 miembros + 1 invitación vigente (la vencida no cuenta) = 3 cupos ocupados
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.update(new UpdateHouseholdCommand(10L, OWNER, "Casa", HomeType.CASA, 2)));

        assertEquals("error.household.residentCount.belowMembers", ex.getCode());
        verify(householdRepository, never()).save(any());
    }

    @Test
    void soloElDuenoPuedeEditarElHogar() {
        when(ownershipChecker.requireOwned(10L, "luis@mail.com")).thenThrow(new HouseholdAccessDeniedException());

        assertThrows(HouseholdAccessDeniedException.class,
                () -> service.update(new UpdateHouseholdCommand(10L, "luis@mail.com", "Casa", HomeType.CASA, 5)));

        verify(householdRepository, never()).save(any());
    }
}