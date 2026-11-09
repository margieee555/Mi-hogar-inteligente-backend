package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.InviteMemberCommand;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.HouseholdMember;
import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationNotifierPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InviteMemberServiceTest {

    private static final String OWNER = "ana@mail.com";

    @Mock private InvitationRepositoryPort invitationRepository;
    @Mock private HouseholdMemberRepositoryPort memberRepository;
    @Mock private UserLookupPort userLookup;
    @Mock private HouseholdOwnershipChecker ownershipChecker;
    @Mock private InvitationNotifierPort notifier;

    private InviteMemberService service;
    private Household hogar;

    @BeforeEach
    void setUp() {
        service = new InviteMemberService(invitationRepository, ownershipChecker, userLookup, memberRepository, notifier);
        hogar = new Household(10L, 1L, "Casa", HomeType.CASA, 3, true, List.of(), LocalDateTime.now());
        when(ownershipChecker.requireOwned(10L, OWNER)).thenReturn(hogar);
        when(userLookup.findUserIdByEmail(any())).thenReturn(Optional.empty());
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of());
    }

    private InviteMemberCommand invitar(String email) {
        return new InviteMemberCommand(10L, OWNER, email);
    }

    @Test
    void guardaUnaInvitacionPendiente() {
        service.invite(invitar("Luis@Mail.com"));

        ArgumentCaptor<Invitation> captor = ArgumentCaptor.forClass(Invitation.class);
        verify(invitationRepository).save(captor.capture());
        assertEquals("luis@mail.com", captor.getValue().getEmail());
        assertEquals(Invitation.Status.PENDING, captor.getValue().getStatus());
        assertEquals(10L, captor.getValue().getHouseholdId());
    }

    @Test
    void noPermiteInvitarseASiMismo() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.invite(invitar("ANA@mail.com")));

        assertEquals("error.invitation.self", ex.getCode());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void noPermiteInvitarAQuienYaEsMiembro() {
        when(userLookup.findUserIdByEmail("luis@mail.com")).thenReturn(Optional.of(7L));
        when(memberRepository.findByHouseholdIdAndUserId(10L, 7L)).thenReturn(Optional.of(
                new HouseholdMember(101L, 10L, 7L, MemberRole.MEMBER, LocalDateTime.now())));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.invite(invitar("luis@mail.com")));

        assertEquals("error.invitation.alreadyMember", ex.getCode());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void noPermiteDuplicarUnaInvitacionPendienteVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of(
                new Invitation(5L, 10L, "luis@mail.com", Invitation.Status.PENDING, ahora, ahora.plusDays(3))));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.invite(invitar("luis@mail.com")));

        assertEquals("error.invitation.alreadyPending", ex.getCode());
    }

    @Test
    void unaInvitacionPendienteVencidaNoImpideInvitarDeNuevo() {
        LocalDateTime ahora = LocalDateTime.now();
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of(
                new Invitation(5L, 10L, "luis@mail.com", Invitation.Status.PENDING,
                        ahora.minusDays(10), ahora.minusDays(3))));

        service.invite(invitar("luis@mail.com"));

        verify(invitationRepository).save(any(Invitation.class));
    }

    @Test
    void soloElDuenoPuedeInvitar() {
        when(ownershipChecker.requireOwned(10L, "otro@mail.com")).thenThrow(new HouseholdAccessDeniedException());

        assertThrows(HouseholdAccessDeniedException.class,
                () -> service.invite(new InviteMemberCommand(10L, "otro@mail.com", "luis@mail.com")));

        verify(invitationRepository, never()).save(any());
    }

        @Test
    void noPermiteInvitarSiElHogarYaCompletoSusResidentes() {
        Household pequeno = new Household(10L, 1L, "Casa", HomeType.CASA, 2, true, List.of(), LocalDateTime.now());
        when(ownershipChecker.requireOwned(10L, OWNER)).thenReturn(pequeno);
        when(memberRepository.findByHouseholdId(10L)).thenReturn(List.of(
                new HouseholdMember(100L, 10L, 1L, MemberRole.OWNER, LocalDateTime.now()),
                new HouseholdMember(101L, 10L, 7L, MemberRole.MEMBER, LocalDateTime.now())));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.invite(invitar("nuevo@mail.com")));

        assertEquals("error.invitation.householdFull", ex.getCode());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void lasInvitacionesPendientesTambienOcupanCupo() {
        Household pequeno = new Household(10L, 1L, "Casa", HomeType.CASA, 2, true, List.of(), LocalDateTime.now());
        LocalDateTime ahora = LocalDateTime.now();
        when(ownershipChecker.requireOwned(10L, OWNER)).thenReturn(pequeno);
        when(memberRepository.findByHouseholdId(10L)).thenReturn(List.of(
                new HouseholdMember(100L, 10L, 1L, MemberRole.OWNER, ahora)));
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of(
                new Invitation(5L, 10L, "pedro@mail.com", Invitation.Status.PENDING, ahora, ahora.plusDays(3))));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.invite(invitar("nuevo@mail.com")));

        assertEquals("error.invitation.householdFull", ex.getCode());
    }

    @Test
    void alInvitarSeAvisaPorCorreo() {
        service.invite(invitar("nuevo@mail.com"));

        verify(notifier).notifyInvitation(eq("nuevo@mail.com"), anyString(), eq(OWNER));
    }
}